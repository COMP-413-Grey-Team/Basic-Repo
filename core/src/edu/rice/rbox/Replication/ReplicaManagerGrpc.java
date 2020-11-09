package edu.rice.rbox.Replication;

import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import edu.rice.rbox.Common.Change.RemoteChange;
import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Common.ServerUUID;
import edu.rice.rbox.ObjStorage.ChangeReceiver;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import edu.rice.rbox.Protos.Generated.RBoxServiceGrpc;
import edu.rice.rbox.Protos.Generated.Rbox;
import org.apache.commons.lang3.SerializationUtils;


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

    private HashMap<GameObjectUUID, List<HolderInfo>> subscribers;      // Primary => Replica
    private HashMap<GameObjectUUID, HolderInfo> publishers;             // Replica => Primary

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
            HolderInfo primaryHolderInfo = new HolderInfo(primaryObjectUUID, serverUUID);
            publishers.put(remoteChange.getTarget(), primaryHolderInfo);

            // Send remote change to storage
            changeReceiver.receiveChange(remoteChange);

        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "failure when sending subscribe request");
        }

    }


    void unsubscribe(GameObjectUUID replicaObjectUUID) {
        // TODO: Build request
        // TODO: Remove from publishers
        // TODO: deleteReplica via changeReceiver (delete replica)
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
            List<HolderInfo> maybeSubscribers = subscribers.get(primaryObjectUUID);
            HolderInfo replicaHolderInfo = new HolderInfo(replica.getTarget(), senderServerUUID);

            if (maybeSubscribers != null) {
                maybeSubscribers.add(replicaHolderInfo);
            } else {
                subscribers.put(primaryObjectUUID, List.of(replicaHolderInfo));
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
            // TODO: Remove from subscribers
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
        subscribers.get(primaryObjectUUID).forEach(holderInfo -> {
            Rbox.ReplicationMessage msg = generateReplicationMessage(holderInfo.getGameObjectUUID(), millis);
            Rbox.UpdateMessage updateMessage = Rbox.UpdateMessage.newBuilder()
                                                   .setRemoteChange(getByteStringFromRemoteChange(remoteChange))
                                                   .setMsg(msg)
                                                   .build();
            getStub(holderInfo.getServerUUID()).handleUpdate(updateMessage, emptyResponseObserver);
        });
    }
}
