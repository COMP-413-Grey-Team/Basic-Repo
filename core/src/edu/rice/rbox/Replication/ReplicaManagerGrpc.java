package edu.rice.rbox.Replication;

import com.google.protobuf.Empty;
import edu.rice.rbox.Common.Change.RemoteChange;
import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Common.ServerUUID;
import edu.rice.rbox.ObjStorage.ChangeReceiver;
import edu.rice.rbox.Protos.Generated.RBoxServiceGrpc;
import edu.rice.rbox.Protos.Generated.Rbox;
import io.grpc.stub.StreamObserver;


import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;


public class ReplicaManagerGrpc {


    private static final Logger logger = Logger.getLogger(ReplicaManagerGrpc.class.getName());

    private ChangeReceiver changeReceiver;
    private HashMap<GameObjectUUID, List<ServerUUID>> subscribers;      // Primary => Replica
    private HashMap<GameObjectUUID, ServerUUID> publishers;             // Replica => Primary
    private HashMap<GameObjectUUID, Integer> timeout;                   //
    private ServerUUID serverUUID;

    /*
     * Constructor
     */
    public ReplicaManagerGrpc(ChangeReceiver changeReceiver) {
        this.changeReceiver = changeReceiver;
    }


    void subscribe(GameObjectUUID primaryObjectUUID, ServerUUID serverUUID) {
        // TODO: Build request => get response via blockingStub.handleSubscribe()
        Rbox.ReplicationMessage msg = Rbox.ReplicationMessage.newBuilder().build();
        Rbox.SubscribeRequest request = Rbox.SubscribeRequest.newBuilder().setMsg(msg).build();
        // TODO: ================================
        // TODO: Add into publishers (Replication)
        // TODO: Get the replica from response and pass replica via changeReceiver(Storage)
    }


    void unsubscribe(GameObjectUUID replicaObjectUUID) {
        // TODO: Build request
        // TODO: Remove from publishers
        // TODO: deleteReplica via changeReceiver (delete replica)
    }


    static class ReplicaManagerImpl extends RBoxServiceGrpc.RBoxServiceImplBase {
        @Override
        public void handleSubscribe(Rbox.SubscribeRequest request,
                                    StreamObserver<Rbox.UpdateMessage> responseObserver) {
            // TODO: getReplica via changeReceiver (Storage) and build response
            // TODO: Add into subscribers
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
    void handleQueryResult() {
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
