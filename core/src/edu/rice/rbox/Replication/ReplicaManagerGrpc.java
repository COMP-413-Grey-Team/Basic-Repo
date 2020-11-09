package edu.rice.rbox.Replication;

import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import edu.rice.rbox.Common.Change.RemoteChange;
import edu.rice.rbox.Common.Change.RemoteDeleteReplicaChange;
import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Common.ServerUUID;
import edu.rice.rbox.ObjStorage.ChangeReceiver;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import edu.rice.rbox.Protos.Generated.RBoxServiceGrpc;
import edu.rice.rbox.Protos.Generated.Rbox;
import org.apache.commons.lang3.SerializationUtils;


import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ReplicaManagerGrpc {


    private static final Logger logger = Logger.getLogger(ReplicaManagerGrpc.class.getName());

    private ChangeReceiver changeReceiver;
    private ServerUUID serverUUID;

    private HashMap<GameObjectUUID, List<ServerUUID>> subscribers;      // Primary => Replica
    private HashMap<GameObjectUUID, ServerUUID> publishers;             // Replica => Primary

    private HashSet<GameObjectUUID> secondaries;                        // Secondary Replicas
    private HashMap<GameObjectUUID, Integer> timeout;                   // Primary => timeout

    private static StreamObserver<Empty> emptyResponseObserver = new StreamObserver<>() {
        @Override
        public void onNext(Empty empty) { }

        @Override
        public void onError(Throwable throwable) { }

        @Override
        public void onCompleted() { }
    };


    /* Constructor */
    public ReplicaManagerGrpc(ChangeReceiver changeReceiver, ServerUUID serverUUID) {
        this.changeReceiver = changeReceiver;
        this.serverUUID = serverUUID;
        this.subscribers = new HashMap<>();
        this.publishers = new HashMap<>();
        this.secondaries = new HashSet<>();
    }

    /* Helper functions */
    private RBoxServiceGrpc.RBoxServiceBlockingStub getBlockingStub(ServerUUID serverUUID) {
        // TODO
        return null;
    }

    private RBoxServiceGrpc.RBoxServiceStub getStub(ServerUUID serverUUID) {
        // TODO
        return null;
    }

    private ByteString getByteStringFromRemoteChange(RemoteChange remoteChange) {
        return ByteString.copyFrom(SerializationUtils.serialize(remoteChange));
    }

    private RemoteChange getRemoteChangeFromUpdateMessage(Rbox.UpdateMessage msg) {
        return SerializationUtils.deserialize(msg.getRemoteChange().toByteArray());
    }

    private GameObjectUUID getGameObjectUUIDFromMessage(Rbox.ReplicationMessage msg) {
        return new GameObjectUUID(UUID.fromString(msg.getTargetObjectUUID()));
    }

    private ServerUUID getServerUUIDFromMessage(Rbox.ReplicationMessage msg) {
        return new ServerUUID(UUID.fromString(msg.getOriginSuperpeerUUID()));
    }

    private Rbox.ReplicationMessage generateReplicationMessage(GameObjectUUID gameObjectUUID) {
        long millis = System.currentTimeMillis();
        return generateReplicationMessage(gameObjectUUID, millis);
    }

    private Rbox.ReplicationMessage generateReplicationMessage(GameObjectUUID gameObjectUUID, long millis) {
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(millis / 1000)
                                  .setNanos((int) ((millis % 1000) * 1000000)).build();
        return Rbox.ReplicationMessage.newBuilder()
                   .setOriginSuperpeerUUID(this.serverUUID.toString())
                   .setTimestamp(timestamp)
                   .setTargetObjectUUID(gameObjectUUID.toString())
                   .build();
    }

    /* Replica Manager functions */
    void subscribe(GameObjectUUID primaryObjectUUID, ServerUUID serverUUID) {
        logger.log(Level.INFO, "Sending subscribe request...");

        // Construct Subscribe Request
        Rbox.ReplicationMessage msg = generateReplicationMessage(primaryObjectUUID);
        Rbox.SubscribeRequest request = Rbox.SubscribeRequest.newBuilder().setMsg(msg).build();
        Rbox.UpdateMessage response;

        try {
            response = getBlockingStub(serverUUID).handleSubscribe(request);
            RemoteChange remoteChange = getRemoteChangeFromUpdateMessage(response);

            // Add into publishers
            publishers.put(remoteChange.getTarget(), serverUUID);

            // Send remote change to storage
            changeReceiver.receiveChange(remoteChange);

        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "failure when sending subscribe request");
        }

    }


    void unsubscribe(GameObjectUUID replicaObjectUUID) {
        logger.info("Sending unsubscribe request...");

        // Construct Unsubscribe Request
        Rbox.ReplicationMessage msg = generateReplicationMessage(replicaObjectUUID);
        Rbox.UnsubscribeRequest request = Rbox.UnsubscribeRequest.newBuilder().setMsg(msg).build();

        try {
            getStub(serverUUID).handleUnsubscribe(request, null);
            Timestamp timestamp = msg.getTimestamp();
            RemoteChange remoteChange = new RemoteDeleteReplicaChange(
                                                replicaObjectUUID,
                                                // convert timestamp from message to Date
                                                Date.from(Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos())));

            // Remove from publishers
            publishers.remove(replicaObjectUUID);

            // Delete replica via changeReceiver (send remote change to storage)
            changeReceiver.receiveChange(remoteChange);

        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed when sending unsubscribe request {0}", e.getStatus());
        }
    }


    class ReplicaManagerImpl extends RBoxServiceGrpc.RBoxServiceImplBase {
        @Override
        public void handleSubscribe(Rbox.SubscribeRequest request,
                                    StreamObserver<Rbox.UpdateMessage> responseObserver) {
            logger.log(Level.INFO, "Handling subscribe request...");

            // Get replica from storage
            GameObjectUUID primaryObjectUUID = getGameObjectUUIDFromMessage(request.getMsg());
            ServerUUID senderServerUUID = getServerUUIDFromMessage(request.getMsg());
            RemoteChange replica = changeReceiver.getReplica(primaryObjectUUID);

            // Add to subscribers
            List<ServerUUID> maybeSubscribers = subscribers.get(primaryObjectUUID);

            if (maybeSubscribers != null) {
                maybeSubscribers.add(senderServerUUID);
            } else {
                subscribers.put(primaryObjectUUID, List.of(senderServerUUID));
            }

            // Send the response
            Rbox.ReplicationMessage message = generateReplicationMessage(primaryObjectUUID);
            ByteString replicaByteString = getByteStringFromRemoteChange(replica);
            Rbox.UpdateMessage response = Rbox.UpdateMessage.newBuilder()
                                             .setMsg(message)
                                             .setRemoteChange(replicaByteString)
                                             .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void handleUnsubscribe(Rbox.UnsubscribeRequest request, StreamObserver<Empty> responseObserver) {
            logger.info("Handling unsubscribe request...");

            GameObjectUUID replicaObjectUUID = getGameObjectUUIDFromMessage(request.getMsg());
            GameObjectUUID primaryObjectUUID = publishers.get(replicaObjectUUID).getGameObjectUUID();

            // Remove from subscribers
            List<HolderInfo> updatedReplicas = subscribers.get(primaryObjectUUID);
            for (HolderInfo holder : subscribers.get(primaryObjectUUID)) {
                if (holder.getGameObjectUUID() == replicaObjectUUID) {
                    updatedReplicas.remove(holder);
                }
            }
            subscribers.put(primaryObjectUUID, updatedReplicas);

        }

        @Override
        public void handleUpdate(Rbox.UpdateMessage request, StreamObserver<Empty> responseObserver) {
            // TODO: Call ChangeReceiver
        }
    }

    void handleQueryResult(GameObjectUUID primaryObjectUUID, List<HolderInfo> interestedObjects) {
        // TODO: Subscribe & Unsubscribe when necessary
    }

    /* Functions for Object Storgae */
    void updatePrimary(RemoteChange change) {
        // TODO: Send change to primary holder
    }

    void broadcastUpdate(RemoteChange change, Boolean interesting) {
        // TODO: Send change to all replica holders
        // TODO: If involves interesting fields change, also send to registrar
    }

    void createPrimary(GameObjectUUID primaryObjectUUID) {
        // No-Op
    }

    void deletePrimary(GameObjectUUID primaryObjectUUID, RemoteChange remoteChange) {
        sendToReplicaHolders(primaryObjectUUID, remoteChange);
        subscribers.remove(primaryObjectUUID);
    }

    /* Helper function for update */
    void sendToReplicaHolders(GameObjectUUID primaryObjectUUID, RemoteChange remoteChange) {
        // Send Update Message to all replica holders
        long millis = System.currentTimeMillis();
        subscribers.get(primaryObjectUUID).forEach(serverUUID -> {
            Rbox.ReplicationMessage msg = generateReplicationMessage(primaryObjectUUID, millis);
            Rbox.UpdateMessage updateMessage = Rbox.UpdateMessage.newBuilder()
                                                   .setRemoteChange(getByteStringFromRemoteChange(remoteChange))
                                                   .setMsg(msg)
                                                   .build();
            getStub(serverUUID).handleUpdate(updateMessage, emptyResponseObserver);
        });
    }
}
