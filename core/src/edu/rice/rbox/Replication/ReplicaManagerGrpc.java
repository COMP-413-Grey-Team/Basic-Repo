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
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ReplicaManagerGrpc {


    private static final Logger logger = Logger.getLogger(ReplicaManagerGrpc.class.getName());

    private ChangeReceiver changeReceiver;
    private ServerUUID serverUUID;

    private HashMap<GameObjectUUID, List<HolderInfo>> subscribers;      // Primary => Replica
    private HashMap<GameObjectUUID, ServerUUID> publishers;             // Replica => Primary


    /*
     * Constructor
     */
    public ReplicaManagerGrpc(ChangeReceiver changeReceiver, ServerUUID serverUUID) {
        this.changeReceiver = changeReceiver;
        this.serverUUID = serverUUID;
    }

    private RBoxServiceGrpc.RBoxServiceBlockingStub getBlockingStub(ServerUUID serverUUID) {
        // TODO
        return null;
    }

    private RBoxServiceGrpc.RBoxServiceStub getStub(ServerUUID serverUUID) {
        // TODO
        return null;
    }

    private Rbox.ReplicationMessage generateReplicationMessage(GameObjectUUID gameObjectUUID) {
        long millis = System.currentTimeMillis();
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(millis / 1000)
                                  .setNanos((int) ((millis % 1000) * 1000000)).build();
        return Rbox.ReplicationMessage.newBuilder()
                   .setOriginSuperpeerUUID(this.serverUUID.toString())
                   .setTimestamp(timestamp)
                   .setTargetObjectUUID(gameObjectUUID.toString())
                   .build();
    }


    void subscribe(GameObjectUUID primaryObjectUUID, ServerUUID serverUUID) {


        // TODO: Build request => get response via blockingStub.handleSubscribe()
        Rbox.ReplicationMessage msg = generateReplicationMessage(primaryObjectUUID);
        Rbox.SubscribeRequest request = Rbox.SubscribeRequest.newBuilder().setMsg(msg).build();
        Rbox.UpdateMessage response;

        try {
            response = getBlockingStub(serverUUID).handleSubscribe(request);
            // TODO: ================================
            // TODO: Add into publishers (Replication)
            // TODO: Get the replica from response and pass replica via changeReceiver(Storage)
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
            // TODO
            GameObjectUUID primaryUUID = new GameObjectUUID(UUID.fromString(request.getMsg().getTargetObjectUUID()));
            RemoteChange replica = changeReceiver.getReplica(primaryUUID);

            // TODO: Add to subscirbers


            // Send the response
            Rbox.ReplicationMessage message = generateReplicationMessage(primaryUUID);
            ByteString replicaByteString = ByteString.copyFrom(SerializationUtils.serialize(replica));
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



    //TODO: need a function to get stub based on server UUID

    /*
     * Function for Object Locator
     */
    void handleQueryResult(GameObjectUUID primaryObjectUUID, List<HolderInfo> interestedObjects) {
        // TODO: Subscribe & Unsubscribe when necessary
    }

    void updatePrimary(RemoteChange change) {
        // TODO: Send change to primary holder
    }

    void broadcastUpdate(RemoteChange change, Boolean interesting) {
        // TODO: Send change to all replica holders
        // TODO: If involves interesting fields change, send to registrar
    }

    void createPrimary(GameObjectUUID id) {
        // TODO: ??????????
    }

    void deletePrimary(GameObjectUUID id) {
        // TODO: send Message to all replica holders
    }
}
