package edu.rice.comp413fall2020grey.ObjStorage;

import edu.rice.comp413fall2020grey.Common.Change.Change;
import edu.rice.comp413fall2020grey.Common.Change.RemoteChange;
import edu.rice.comp413fall2020grey.Common.GameObjectMetadata;
import edu.rice.comp413fall2020grey.Common.GameObjectUUID;

import edu.rice.comp413fall2020grey.Common.Change.LocalChange;
import edu.rice.comp413fall2020grey.Common.Mode;
import java.io.Serializable;
import java.time.Instant;
import java.util.*;

public class ObjectStore implements DistributedManager{

    ObjectStorageReplicationInterface replicaManager;
    ArrayList<HashMap<GameObjectUUID, HashMap<String, Serializable>>> store;
    HashMap<GameObjectUUID, GameObjectMetadata> metadata;
    int bufferStart = 0; // to be replaced by a circular buffer
    ArrayList<Date> bufferLag;

    private int circ(int index) {
        return (bufferStart + index) % store.size();
    }
    private int getBufferIndex(Date now){
        for (int i = 0; i < bufferLag.size(); i++) {
            if (bufferLag.get(circ(i)).after(now)) {
                return i;
            }
        }
        return bufferLag.size() - 1;
    }

    public ObjectStore(ObjectStorageReplicationInterface replicaManager) {
        this.replicaManager = replicaManager;
    }

    @Override
    public Set<LocalChange> synchronize() {
        Set<RemoteChange> remoteChanges = replicaManager.flushCache();
        Set<LocalChange> localChanges = new HashSet<>();
        for (RemoteChange remoteChange: remoteChanges) {
            int i = getBufferIndex(remoteChange.getTimestamp());
            store.get(i).get(remoteChange.getTarget()).put(remoteChange.getField(), remoteChange.getValue());
            localChanges.add(new LocalChange(remoteChange.getTarget(), remoteChange.getField(), remoteChange.getValue(), i));
        }
        return localChanges;
    }

    @Override
    public void advanceBuffer() {
        bufferStart = (bufferStart - 1) % store.size();
        store.set(bufferStart, store.get((bufferStart - 1) % store.size()));
        bufferLag.set(bufferStart, Date.from(Instant.now()));
    }

    @Override
    public Serializable read(GameObjectUUID gameObjectID, String field, int bufferIndex) {
        return store.get(circ(bufferIndex)).get(gameObjectID).get(field);
    }

    @Override
    public boolean write(LocalChange change, GameObjectUUID author) {
        switch (metadata.get(author).getMode()) {

            // TODO: Handle case where target does not yet exist. (Create new primary)
            case PRIMARY:
                // Record change in local store.
                store.get(circ(change.getBufferIndex())).get(change.getTarget()).put(change.getField(), change.getValue());
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
                    store.get(circ(change.getBufferIndex() + bufferStart)).get(change.getTarget()).put(change.getField(), change.getValue());
                    return true;
                }
            default:    // Defaults to false.
                return false;
        }
    }

    @Override
    public boolean lagWrite(RemoteChange change, GameObjectUUID author) {
        return write(new LocalChange(change.getTarget(), change.getField(), change.getValue(), getBufferIndex(change.getTimestamp())), author);
    }

    @Override
    public GameObjectUUID create(HashMap<String, Serializable> fields, GameObjectUUID author, int bufferIndex){
        if (metadata.get(author).getMode() == Mode.PRIMARY) {
            GameObjectUUID uuid = GameObjectUUID.randomUUID();
            store.get(circ(bufferIndex)).put(uuid, fields);
            //TODO
            //inform replica management about new primary
            return uuid;
        } else {
            return null;
        }
    }

    @Override
    public boolean delete(GameObjectUUID uuid, GameObjectUUID author, int bufferIndex){
        if (metadata.get(author).getMode() == Mode.PRIMARY) {
            store.get(circ(bufferStart)).remove(uuid);
            //TODO
            //inform replica management that primary is removed
            return true;
        } else {
            return false;
        }
    }
}