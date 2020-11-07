package edu.rice.rbox.ObjStorage;

import edu.rice.rbox.Common.Change.RemoteChange;
import edu.rice.rbox.Common.GameField;
import edu.rice.rbox.Common.GameObjectUUID;

import java.io.Serializable;
import java.util.HashMap;

public class DummyReplicaManager implements ObjectStorageReplicationInterface {
    @Override
    public void updatePrimary(RemoteChange change) {

    }

    @Override
    public void broadcastUpdate(RemoteChange change, Boolean interesting) {

    }

    @Override
    public void createPrimary(GameObjectUUID id, HashMap<String, GameField> interestingField, String predicate) {

    }

    @Override
    public void deletePrimary(GameObjectUUID id, RemoteChange change) {

    }
}