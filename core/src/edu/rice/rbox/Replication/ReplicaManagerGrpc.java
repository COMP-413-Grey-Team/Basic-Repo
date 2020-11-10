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
            logger.log(Level.INFO, "Handling update...");

            RemoteChange change = getRemoteChangeFromUpdateMessage(request);

            // Call ChangeReceiver
            changeReceiver.receiveChange(change);

            // Check if secondary, and if so notify registrar
            if (secondaries.contains(change.getTarget())) {
                // TODO: Implement once registrar interface exists
            }

            // Check if deleting replica and if so update publishers
            // TODO: How do we check if change is DeleteReplicaChange?
        }
    }

    void handleQueryResult(GameObjectUUID primaryObjectUUID, List<edu.rice.rbox.Replication.HolderInfo> interestedObjects) {
        // TODO: Subscribe & Unsubscribe when necessary
    }

    /* Functions for Object Storage */
    void updatePrimary(RemoteChange change) {
        // Send the response
        logger.log(Level.INFO, "Sending change to primary...");

        // Get primary
        GameObjectUUID targetUuid = change.getTarget();
        ServerUUID info = publishers.get(targetUuid);
        ByteString changeByteString = getByteStringFromRemoteChange(change);
        long millis = System.currentTimeMillis();
        Rbox.ReplicationMessage msg = generateReplicationMessage(targetUuid, millis);

        Rbox.UpdateMessage request = Rbox.UpdateMessage.newBuilder()
                                               .setRemoteChange(changeByteString)
                                               .setMsg(msg)
                                               .build();
        Rbox.UpdateMessage response;
        try {
            getStub(targetUuid).handleUpdate(request);

        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "failure when sending update");
        }

    }

    void broadcastUpdate(RemoteChange change, Boolean interesting) {
        // Send change to all replica holders
        sendToReplicaHolders(change.getTarget(), change);

        // TODO: If involves interesting fields change, also send to registrar
        if (interesting) {

        }
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
            getBlockingStub(holderInfo.getServerUUID()).handleUpdate(updateMessage);
        });
    }
}
