package edu.rice.comp413fall2020grey.ObjStorage;

import edu.rice.comp413fall2020grey.Common.GameObjectUUID;

import java.io.Serializable;

public class DummyReplicaManager implements ObjectStorageReplicationInterface {

    @Override
    public void updatePrimary(GameObjectUUID id, String field, Serializable value, Boolean interesting) {

    }

    @Override
    public void broadcastUpdate(GameObjectUUID id, String field, Serializable value, Boolean interesting) {

    }

    @Override
    public void createPrimary(GameObjectUUID id) {

    }

    @Override
    public void deletePrimary(GameObjectUUID id) {

    }
}