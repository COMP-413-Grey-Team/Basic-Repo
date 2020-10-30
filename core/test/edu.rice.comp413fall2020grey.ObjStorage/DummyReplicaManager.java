package edu.rice.comp413fall2020grey.ObjStorage;

import edu.rice.comp413fall2020grey.Common.Change.RemoteChange;
import edu.rice.comp413fall2020grey.Common.GameObjectUUID;

import java.io.Serializable;
import java.util.HashMap;

public class DummyReplicaManager implements ObjectStorageReplicationInterface {

    @Override
    public void updatePrimary(RemoteChange change, Boolean interesting) {

    }

    @Override
    public void broadcastUpdate(RemoteChange change, Boolean interesting) {

    }

    @Override
    public void createPrimary(GameObjectUUID id, HashMap<String, Serializable> interestingFields) {

    }

    @Override
    public void deletePrimary(GameObjectUUID id) {

    }
}