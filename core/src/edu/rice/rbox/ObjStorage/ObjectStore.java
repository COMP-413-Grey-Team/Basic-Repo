package edu.rice.rbox.ObjStorage;

import edu.rice.rbox.Common.Change.*;
import edu.rice.rbox.Common.GameObjectUUID;

import edu.rice.rbox.Common.Mode;
import java.io.Serializable;
import java.time.Instant;
import java.util.*;

public class ObjectStore implements DistributedManager, ChangeReceiver {

    private ObjectStorageReplicationInterface replicaManager;
    private ArrayList<HashMap<GameObjectUUID, HashMap<String, Serializable>>> store;
    private int bufferStart = 0; // to be replaced by a circular buffer
    private ArrayList<Date> bufferLag;
    private ArrayList<RemoteChange> remoteChangeBuffer = new ArrayList<>();

    public static String INTERESTING_FIELDS = "rbox_interesting_fields"; // HashSet<String>
    public static String MODE = "rbox_mode"; // Mode
    public static String PREDICATE = "rbox_predicate";

    private int circ(int index) {
        return (bufferStart + index) % store.size();
    }

    public int getBufferIndex(Date now){
        for (int i = 0; i < bufferLag.size(); i++) {
            System.out.println(bufferLag.get(circ(i)).toInstant());
            if (bufferLag.get(circ(i)).compareTo(now) >= 0) {
                return i;
            }
        }
        return -1;
    }

    public ObjectStore(ObjectStorageReplicationInterface replicaManager, int size) {
        this.replicaManager = replicaManager;
        this.store = new ArrayList<>(size);
        this.bufferLag = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
           store.add(i, new HashMap<>());
           bufferLag.add(i, Date.from(Instant.now()));
        }
    }

    @Override
    public Set<LocalChange> synchronize() {
        Set<LocalChange> localChanges = new HashSet<>();
        remoteChangeBuffer.forEach(change -> localChanges.add(applyRemoteChange(change)));
        remoteChangeBuffer = new ArrayList<>();
        return localChanges;
    }

    private LocalChange applyRemoteChange(RemoteChange change) {
        final int bufferIndex = getBufferIndex(change.getTimestamp());
        if (change instanceof RemoteAddReplicaChange) {
            final LocalAddReplicaChange
                localChange =
                new LocalAddReplicaChange(((RemoteAddReplicaChange) change).getTarget(), ((RemoteAddReplicaChange) change).getObject(), bufferIndex);
            store.get(bufferIndex).put(localChange.getTarget(), localChange.getObject());
            return localChange;
        } else if (change instanceof RemoteDeleteReplicaChange) {
            final LocalDeleteReplicaChange
                localChange =
                new LocalDeleteReplicaChange(((RemoteDeleteReplicaChange) change).getTarget(), bufferIndex);
            store.get(bufferIndex).remove(localChange.getTarget());
            return localChange;
        } else if (change instanceof RemoteFieldChange) {
            final LocalFieldChange
                localChange =
                new LocalFieldChange(((RemoteFieldChange) change).getTarget(),
                    ((RemoteFieldChange) change).getField(),
                    ((RemoteFieldChange) change).getValue(),
                    bufferIndex);
            store.get(bufferIndex).get(localChange.getTarget()).put(localChange.getField(), localChange.getValue());
            return localChange;
        } else {
            throw new IllegalStateException("Unknown change of type " + change.getClass());
        }
    }

    @Override
    public void advanceBuffer() {
        bufferStart = (bufferStart - 1 + store.size()) % store.size();
        HashMap<GameObjectUUID, HashMap<String, Serializable>> asdf = new HashMap<>();
        store.get(circ(1)).entrySet().forEach(entry -> asdf.put(entry.getKey(),
                (HashMap<String, Serializable>) entry.getValue().clone()));
        store.set(bufferStart, asdf);
        bufferLag.set(bufferStart, Date.from(Instant.now()));
    }

    @Override
    public Serializable read(GameObjectUUID gameObjectID, String field, int bufferIndex) {
        return store.get(circ(bufferIndex)).get(gameObjectID).get(field);
    }

    @Override
    public RemoteChange getReplica(GameObjectUUID id) {
        return new RemoteAddReplicaChange(id, store.get(circ(0)).get(id), Date.from(Instant.now()));
    }

    @Override
    public boolean write(LocalFieldChange change, GameObjectUUID author) {
        HashMap<GameObjectUUID, HashMap<String, Serializable>> state = store.get(circ(change.getBufferIndex()));
        if (state.get(author).get(MODE) == Mode.PRIMARY) {
            // Record change in local store.
            state.get(change.getTarget()).put(change.getField(), change.getValue());
            Boolean interesting = ((HashSet<String>)state.get(change.getTarget()).get(INTERESTING_FIELDS)).contains(change.getField());
            // Propagate update
            if (state.get(change.getTarget()).get(MODE) == Mode.PRIMARY) {
                replicaManager.broadcastUpdate(new RemoteFieldChange(change.getTarget(), change.getField(), change.getValue(), Date.from(Instant.now())), interesting);
            } else {
                replicaManager.updatePrimary(new RemoteFieldChange(change.getTarget(), change.getField(), change.getValue(), Date.from(Instant.now())), interesting);
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
    public GameObjectUUID create(HashMap<String, Serializable> fields, HashSet<String> interesting_fields, GameObjectUUID author, int bufferIndex) {
        HashMap<GameObjectUUID, HashMap<String,Serializable>> state = store.get(circ(bufferIndex));
        if (author == null || state.get(author).get(MODE) == Mode.PRIMARY) {
            GameObjectUUID uuid = GameObjectUUID.randomUUID();
            fields.put(INTERESTING_FIELDS, interesting_fields);
            fields.put(MODE, Mode.PRIMARY);
            state.put(uuid, fields);
            HashMap<String, Serializable> interesting_state = new HashMap<>();
            fields.entrySet().forEach(entry -> {
                if (interesting_fields.contains(entry.getKey())) {
                   interesting_state.put(entry.getKey(), entry.getValue()) ;
                }
            });
            replicaManager.createPrimary(uuid, interesting_state);
            return uuid;
        } else {
            return null;
        }
    }

    @Override
    public boolean delete(GameObjectUUID uuid, GameObjectUUID author, int bufferIndex){
        if (store.get(circ(bufferIndex)).get(author).get(MODE) == Mode.PRIMARY) {
            store.get(circ(bufferIndex)).remove(uuid);
            replicaManager.deletePrimary(uuid);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void receiveChange(RemoteChange change) {
        remoteChangeBuffer.add(change);
    }

}