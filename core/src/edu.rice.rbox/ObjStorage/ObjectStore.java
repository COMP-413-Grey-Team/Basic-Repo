package edu.rice.rbox.ObjStorage;

<<<<<<< HEAD:core/src/edu/rice/rbox/ObjStorage/ObjectStore.java
import edu.rice.rbox.Common.Change.*;
import edu.rice.rbox.Common.GameObjectUUID;

import edu.rice.rbox.Common.Mode;
import java.io.Serializable;
=======
import edu.rice.comp413fall2020grey.Common.Change.*;
import edu.rice.comp413fall2020grey.Common.GameField;
import edu.rice.comp413fall2020grey.Common.GameObjectUUID;

import edu.rice.comp413fall2020grey.Common.Mode;

>>>>>>> ce7c210... getting all changes from master:core/src/edu/rice/comp413fall2020grey/ObjStorage/ObjectStore.java
import java.time.Instant;
import java.util.*;

public class ObjectStore implements DistributedManager, ChangeReceiver {

    private ObjectStorageReplicationInterface replicaManager;
    private ArrayList<HashMap<GameObjectUUID, HashMap<String, GameField>>> store;
    private HashMap<GameObjectUUID, Mode> objectModes;
    private HashMap<GameObjectUUID, HashSet<String>> objectInterestingFields;

    private int bufferStart = 0; // to be replaced by a circular buffer
    private ArrayList<Date> bufferLag;
    private ArrayList<RemoteChange> remoteChangeBuffer = new ArrayList<>();

    private int circ(int index) {
        return (bufferStart + index) % store.size();
    }

    public int getBufferIndex(Date now) {
        for (int i = 0; i < bufferLag.size(); i++) {
            //System.out.println(bufferLag.get(circ(i)).toInstant());
            if (bufferLag.get(circ(i)).compareTo(now) >= 0) {
                return i;
            }
        }
        return -1;
    }

    public ObjectStore(ObjectStorageReplicationInterface replicaManager, int size) {
        this.replicaManager = replicaManager;
        this.store = new ArrayList<>(size);
        this.objectModes = new HashMap<>();
        this.objectInterestingFields = new HashMap<>();
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
        localChanges.remove(null);
        return localChanges;
    }

    private LocalChange applyRemoteChange(RemoteChange change) {
        final int bufferIndex = getBufferIndex(change.getTimestamp());
        if (change instanceof RemoteAddReplicaChange) {
            final LocalAddReplicaChange
                    localChange =
                    new LocalAddReplicaChange(change.getTarget(), ((RemoteAddReplicaChange) change).getObject(), bufferIndex);
            store.get(bufferIndex).put(localChange.getTarget(), localChange.getObject());
            objectModes.put(localChange.getTarget(), Mode.REPLICA);
            return localChange;
        } else if (change instanceof RemoteDeleteReplicaChange) {
            if (objectModes.get(change.getTarget()) == Mode.REPLICA) {
                final LocalDeleteReplicaChange
                        localChange =
                        new LocalDeleteReplicaChange(change.getTarget(), bufferIndex);
                for (int i = 0; i < store.size(); i++) {
                    store.get(i).remove(localChange.getTarget());
                }
                objectModes.remove(localChange.getTarget());
                objectInterestingFields.remove(localChange.getTarget());
                return localChange;
            } else {
                return null;
            }
        } else if (change instanceof RemoteFieldChange) {
            final LocalFieldChange
                    localChange =
                    new LocalFieldChange(change.getTarget(),
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
        HashMap<GameObjectUUID, HashMap<String, GameField>> newState = new HashMap<>();
        store.get(circ(1)).forEach((key, value) -> {
            HashMap<String, GameField> gameObject = new HashMap<>();
            value.forEach((field_key, field_value) -> gameObject.put(field_key, field_value.copy()));
            newState.put(key, gameObject);
        });
        store.set(bufferStart, newState);
        bufferLag.set(bufferStart, Date.from(Instant.now()));
    }

    @Override
    public GameField read(GameObjectUUID gameObjectID, String field, int bufferIndex) {
        return store.get(circ(bufferIndex)).get(gameObjectID).get(field);
    }

    @Override
    public RemoteChange getReplica(GameObjectUUID id) {
        return new RemoteAddReplicaChange(id, store.get(circ(0)).get(id), Date.from(Instant.now()));
    }

    @Override
    public boolean write(LocalChange change, GameObjectUUID author) {
        if (change instanceof LocalFieldChange) {
            LocalFieldChange fieldChange = (LocalFieldChange) change;
            HashMap<GameObjectUUID, HashMap<String, GameField>> state = store.get(circ(change.getBufferIndex()));
            if (objectModes.get(author) == Mode.PRIMARY) {
                // Record change in local store.
                state.get(fieldChange.getTarget()).put(fieldChange.getField(), fieldChange.getValue());
                Boolean interesting = objectInterestingFields.get(fieldChange.getTarget()).contains(fieldChange.getField());
                // Propagate update
                if (objectModes.get(fieldChange.getTarget()) == Mode.PRIMARY) {
                    replicaManager.broadcastUpdate(new RemoteFieldChange(fieldChange.getTarget(), fieldChange.getField(), fieldChange.getValue(), Date.from(Instant.now())), interesting);
                } else {
                    replicaManager.updatePrimary(new RemoteFieldChange(fieldChange.getTarget(), fieldChange.getField(), fieldChange.getValue(), Date.from(Instant.now())));
                }
                return true;
            } else {
                if (objectModes.get(fieldChange.getTarget()) == Mode.PRIMARY) {
                    return false;   // Reject update.
                } else {    // Record change locally, but do not propagate.
                    state.get(fieldChange.getTarget()).put(fieldChange.getField(), fieldChange.getValue());
                    return true;
                }
            }
        } else if (change instanceof LocalAddReplicaChange) {
            LocalAddReplicaChange addReplicaChange = (LocalAddReplicaChange) change;
            store.get(circ(addReplicaChange.getBufferIndex())).put(addReplicaChange.getTarget(), addReplicaChange.getObject());
            objectModes.put(addReplicaChange.getTarget(), Mode.REPLICA);
            return true;
        } else if (change instanceof LocalDeleteReplicaChange && objectModes.get(change.getTarget()) == Mode.REPLICA) {
            for (int i = 0; i < store.size(); i++) {
                store.get(i).remove(change.getTarget());
            }
            objectModes.remove(change.getTarget());
            objectInterestingFields.remove(change.getTarget());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public GameObjectUUID create(HashMap<String, GameField> fields, HashSet<String> interesting_fields,
                                 String predicate, GameObjectUUID author, int bufferIndex) {
        HashMap<GameObjectUUID, HashMap<String, GameField>> state = store.get(circ(bufferIndex));
        if (author == null || objectModes.get(author) == Mode.PRIMARY) {
            GameObjectUUID uuid = GameObjectUUID.randomUUID();
            objectInterestingFields.put(uuid, interesting_fields);
            objectModes.put(uuid, Mode.PRIMARY);
            state.put(uuid, fields);
            HashMap<String, GameField> interesting_state = new HashMap<>();
            fields.forEach((key, value) -> {
                if (interesting_fields.contains(key)) {
                    interesting_state.put(key, value);
                }
            });
            replicaManager.createPrimary(uuid, interesting_state, predicate);
            return uuid;
        } else {
            return null;
        }
    }

    @Override
    public boolean delete(GameObjectUUID uuid, GameObjectUUID author, int bufferIndex) {
        if (objectModes.get(author) == Mode.PRIMARY) {
            for (int i = 0; i < store.size(); i++) {
                store.get(i).remove(uuid);
            }
            objectInterestingFields.remove(uuid);
            objectModes.remove(uuid);
            RemoteDeleteReplicaChange change = new RemoteDeleteReplicaChange(uuid, Date.from(Instant.now()));
            replicaManager.deletePrimary(uuid, change);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void receiveChange(RemoteChange change) {
        remoteChangeBuffer.add(change);
    }

    @Override
    public void deleteReplica(GameObjectUUID id, Date timestamp) {
        remoteChangeBuffer.add(new RemoteDeleteReplicaChange(id, Date.from(Instant.now())));
    }

    @Override
    public void promoteSecondary(GameObjectUUID id) {
        objectModes.put(id, Mode.PRIMARY);
    }
}