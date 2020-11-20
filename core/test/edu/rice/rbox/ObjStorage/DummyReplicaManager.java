package edu.rice.rbox.ObjStorage;

import edu.rice.rbox.Common.Change.RemoteChange;
import edu.rice.rbox.Common.GameField.GameField;
import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Location.interest.InterestPredicate;

import java.util.HashMap;

public class DummyReplicaManager implements ObjectStorageReplicationInterface {
    @Override
    public void updatePrimary(RemoteChange change) {

    }

    @Override
    public void broadcastUpdate(RemoteChange change) {

    }

    @Override
    public void createPrimary(GameObjectUUID id) {

    }

    @Override
    public void deletePrimary(GameObjectUUID id, RemoteChange change) {

    }
}