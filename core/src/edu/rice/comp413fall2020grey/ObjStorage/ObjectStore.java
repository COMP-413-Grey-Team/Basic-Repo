package edu.rice.comp413fall2020grey.ObjStorage;

import edu.rice.comp413fall2020grey.Common.Change.Change;
import edu.rice.comp413fall2020grey.Common.GameObjectMetadata;
import edu.rice.comp413fall2020grey.Common.GameObjectUUID;

import edu.rice.comp413fall2020grey.Common.Change.LocalChange;
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
    public boolean write(LocalChange change, GameObjectUUID author) {
        switch (metadata.get(author).getMode()) {

            // TODO: Handle case where target does not yet exist. (Create new primary)
            case PRIMARY:
                // Record change in local store.
                store.get(change.getBufferIndex()).get(change.getTarget()).put(change.getField(), change.getValue());
                // Propagate update.
                if (metadata.get(change.getTarget()).getMode() == Mode.PRIMARY) {
                    replicaManager.broadcastUpdate(change.getTarget(), change.getField(), change.getValue());
                } else {
                    replicaManager.updatePrimary(change.getTarget(), change.getField(), change.getValue());
                }
                return true;
            case REPLICA:
            case SECONDARY:
                if (metadata.get(change.getTarget()).getMode() == Mode.PRIMARY) {
                    return false;   // Reject update.
                } else {    // Record change locally, but do not propagate.
                    store.get(change.getBufferIndex()).get(change.getTarget()).put(change.getField(), change.getValue());
                    return true;
                }
            default:    // Defaults to false.
                return false;
        }
    }
}
