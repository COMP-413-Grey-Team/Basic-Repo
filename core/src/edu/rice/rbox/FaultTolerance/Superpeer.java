package edu.rice.rbox.FaultTolerance;

import com.google.protobuf.Timestamp;
import edu.rice.rbox.Common.Change.RemoteChange;
import edu.rice.rbox.Common.GameField.InterestingGameField;
import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Location.interest.InterestPredicate;
import edu.rice.rbox.ObjStorage.ChangeReceiver;
import edu.rice.rbox.ObjStorage.ObjectStorageLocationInterface;
import edu.rice.rbox.ObjStorage.ObjectStorageReplicationInterface;
import edu.rice.rbox.Replication.ReplicaManagerGrpc;
import edu.rice.rbox.ObjStorage.ObjectStore;
import edu.rice.rbox.Common.ServerUUID;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import network.RBoxProto;
import network.RegistrarGrpc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Superpeer {
    private final int storeSize = 100;
    private final int port = 3000;

    private ServerUUID serverUUID;
    private ReplicaManagerGrpc replicaManager;
    private ObjectStore store;

    /* Constructor of the Superpeer, setting up the adaptors */
    public Superpeer() {
        this.serverUUID = ServerUUID.randomUUID();
        this.replicaManager = new ReplicaManagerGrpc(
            port,
            new ChangeReceiver() {
            @Override
            public void receiveChange(RemoteChange change) {

            }

            @Override
            public void deleteReplica(GameObjectUUID id, Date timestamp) {

            }

            @Override
            public RemoteChange getReplica(GameObjectUUID id) {
                return null;
            }

            @Override
            public void promoteSecondary(GameObjectUUID id) {

            }
        },
            serverUUID);

        this.store = new ObjectStore(
            new ObjectStorageReplicationInterface() {
            @Override
            public void updatePrimary(RemoteChange change) {
                replicaManager.updatePrimary(change);
            }

            @Override
            public void broadcastUpdate(RemoteChange change) {
                replicaManager.broadcastUpdate(change);
            }

            @Override
            public void createPrimary(GameObjectUUID id) {
                // No-op for replica manager
            }

            @Override
            public void deletePrimary(GameObjectUUID id, RemoteChange change) {
                replicaManager.deletePrimary(id, change);
            }
        },
            new ObjectStorageLocationInterface() {
            @Override
            public void add(GameObjectUUID id,
                            InterestPredicate predicate,
                            HashMap<String, InterestingGameField> value) {

            }

            @Override
            public void update(GameObjectUUID id, String field, InterestingGameField value) {

            }

            @Override
            public void delete(GameObjectUUID id) {

            }

            @Override
            public void queryInterest() {

            }
        },
            storeSize);
    }

    private void start(String registrarIP) throws Exception {
        replicaManager.start(registrarIP);
        replicaManager.blockUntilShutdown();

    }


    /* The first element of args is the ip address of the registrar */
    public static void main( String[] args ) throws Exception {
        if (args.length != 1) {
            System.err.println("Please provide the ip address of the registrar");
            System.exit(1);
        }

        Superpeer superpeer = new Superpeer();
        String registrarIP = args[0];
        superpeer.start(registrarIP);
    }
}
