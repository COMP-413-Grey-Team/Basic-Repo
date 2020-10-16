package edu.rice.comp413fall2020grey.ObjStorage;

import edu.rice.comp413fall2020grey.Common.Change.Change;
import edu.rice.comp413fall2020grey.Common.Change.RemoteChange;
import edu.rice.comp413fall2020grey.Common.GameObject;
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
    int bufferStart = 0; // to be replaced by a circular buffer
    ArrayList<Date> bufferLag;

    public static String INTERESTING_FIELDS = "rbox_interesting_fields"; // HashSet<String>
    public static String MODE = "rbox_mode"; // Mode
    public static String PREDICATE = "rbox_predicate";

    private int circ(int index) {
        return (bufferStart + index) % store.size();
    }

    public int getBufferIndex(Date now){
        for (int i = 0; i < bufferLag.size(); i++) {
            if (bufferLag.get(circ(i)).after(now)) {
                return i;
            }
        }
        return -1;
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
        HashMap<GameObjectUUID, HashMap<String, Serializable>> state = store.get(circ(change.getBufferIndex()));
        if (state.get(author).get(MODE) == Mode.PRIMARY) {
            // TODO: Handle case where target does not yet exist. (Create new primary)
            // Record change in local store.
            state.get(change.getTarget()).put(change.getField(), change.getValue());
            Boolean interesting = ((HashSet<String>)state.get(change.getTarget()).get(INTERESTING_FIELDS)).contains(change.getField());
            // Propagate update
            if (state.get(change.getTarget()).get(MODE) == Mode.PRIMARY) {
                replicaManager.broadcastUpdate(change.getTarget(), change.getField(), change.getValue(), interesting);
            } else {
                replicaManager.updatePrimary(change.getTarget(), change.getField(), change.getValue(), interesting);
            }
            return true;
        } else {
            if (state.get(change.getTarget()).get(MODE) == Mode.PRIMARY) {
                return false;   // Reject update.
            } else {    // Record change locally, but do not propagate.
                state.get(change.getTarget()).put(change.getField(), change.getValue());
                return true;
            }
        }
    }

    @Override
    public GameObjectUUID create(HashMap<String, Serializable> fields, HashSet<String> interesting_fields, GameObjectUUID author, int bufferIndex){
        HashMap<GameObjectUUID, HashMap<String,Serializable>> state = store.get(circ(bufferIndex));
        if (state.get(author).get(MODE) == Mode.PRIMARY) {
            GameObjectUUID uuid = GameObjectUUID.randomUUID();
            interesting_fields.add(MODE);
            interesting_fields.add(INTERESTING_FIELDS);
            interesting_fields.add(PREDICATE);
            fields.put(INTERESTING_FIELDS, interesting_fields);
            state.put(uuid, fields);
            //TODO
            //inform replica management about new primary
            return uuid;
        } else {
            return null;
        }
    }

    @Override
    public boolean delete(GameObjectUUID uuid, GameObjectUUID author, int bufferIndex){
        if (store.get(circ(bufferIndex)).get(author).get(MODE) == Mode.PRIMARY) {
            store.get(circ(bufferIndex)).remove(uuid);
            //TODO
            //inform replica management that primary is removed
            return true;
        } else {
            return false;
        }
    }
}