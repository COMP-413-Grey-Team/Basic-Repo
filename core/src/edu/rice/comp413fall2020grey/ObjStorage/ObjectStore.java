package edu.rice.comp413fall2020grey.ObjStorage;

import edu.rice.comp413fall2020grey.Common.GameObjectMetadata;
import edu.rice.comp413fall2020grey.Common.GameObjectUUID;

import edu.rice.comp413fall2020grey.Common.Mode;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class ObjectStore implements DistributedManager{

    ObjectStorageReplicationInterface replicaManager;
    ArrayList<HashMap<GameObjectUUID, HashMap<String, Serializable>>> store;
    HashMap<GameObjectUUID, GameObjectMetadata> metadata;

    public ObjectStore(ObjectStorageReplicationInterface replicaManager) {
        this.replicaManager = replicaManager;
    }

    @Override
    public Set<Change> synchronize() {
        return null;
    }

    @Override
    public void advanceBuffer() {

    }

    @Override
    public Serializable read(GameObjectUUID gameObjectID, String field, int bufferIndex) {
        return store.get(bufferIndex).get(gameObjectID).get(field);
    }

    @Override
    public boolean write(Change change, GameObjectUUID author) {
        switch (metadata.get(author).getMode()) {

            // TODO: Handle case where target does not yet exist. (Create new primary)
            case PRIMARY:
                // Case where target exists in store.
                store.get(change.bufferIndex).get(change.target).put(change.field, change.value);
                // Propagate update.
                if (metadata.get(change.target).getMode() == Mode.PRIMARY) {
                    replicaManager.broadcastUpdate(change.target, change.field, change.value);
                } else {
                    replicaManager.updatePrimary(change.target, change.field, change.value);
                }
                return true;
            case REPLICA:
            case SECONDARY:
                if (metadata.get(change.target).getMode() == Mode.PRIMARY) {
                    return false;   // Reject update.
                } else {    // Update accepted but not propagated.
                    store.get(change.bufferIndex).get(change.target).put(change.field, change.value);
                    return true;
                }
            default:    // Defaults to false.
                return false;
        }
    }
}
