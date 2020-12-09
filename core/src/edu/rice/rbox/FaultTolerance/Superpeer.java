package edu.rice.rbox.FaultTolerance;

import edu.rice.rbox.Common.Change.RemoteChange;
import edu.rice.rbox.Common.GameField.InterestingGameField;
import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Game.Server.GameStateManager;
import edu.rice.rbox.Location.interest.InterestPredicate;
import edu.rice.rbox.Location.locator.LocatorMainImpl;
import edu.rice.rbox.ObjStorage.*;
import edu.rice.rbox.Replication.HolderInfo;
import edu.rice.rbox.Replication.ObjectLocationReplicationInterface;
import edu.rice.rbox.Replication.ReplicaManagerGrpc;
import edu.rice.rbox.Common.ServerUUID;

import java.util.*;


public class Superpeer {
    private final int storeSize = 100;
    private final int port = 3000;

    private ServerUUID serverUUID;
    private ReplicaManagerGrpc replicaManager;
    private ObjectStore store;
    private LocatorMainImpl locator;
    private GameStateManager gameStateManager;

    /* Constructor of the Superpeer, setting up the adaptors */
    public Superpeer() {
        this.serverUUID = ServerUUID.randomUUID();
        this.replicaManager = new ReplicaManagerGrpc(port, serverUUID,
            new ChangeReceiver() {
            @Override
            public void receiveChange(RemoteChange change) {
                store.receiveChange(change);
            }

            @Override
            public void deleteReplica(GameObjectUUID id, Date timestamp) {
                store.deleteReplica(id, timestamp);
            }

            @Override
            public RemoteChange getReplica(GameObjectUUID id) {
                return store.getReplica(id);
            }

            @Override
            public void promoteSecondary(GameObjectUUID id) {
                store.promoteSecondary(id);
            }
        });

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
                locator.add(id, predicate, value);
            }

            @Override
            public void update(GameObjectUUID id, String field, InterestingGameField value) {
                locator.update(id, field, value);
            }

            @Override
            public void delete(GameObjectUUID id) {
                locator.delete(id);
            }

            @Override
            public void queryInterest() {
                locator.queryInterest();
            }
        },
            storeSize);

        this.locator = new LocatorMainImpl(serverUUID,
            (id, field) -> store.queryOneField(id, field),
            interestedObjects -> replicaManager.handleQueryResult(interestedObjects)
        );
        this.gameStateManager = new GameStateManager(serverUUID, this.store);
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
        Set<Integer> myRooms = new HashSet<>(superpeer.replicaManager.getAssignedRooms());
        System.out.println("Number of rooms assigned to this superpeer: " + myRooms.size());
        superpeer.gameStateManager.initializeRooms(myRooms);
    }
}
