package edu.rice.rbox.ObjStorage;

import edu.rice.rbox.Common.Change.*;
import edu.rice.rbox.Common.GameField.GameField;
import edu.rice.rbox.Common.GameObjectUUID;

import edu.rice.rbox.Common.InterestingGameField;
import edu.rice.rbox.Common.Mode;
import edu.rice.rbox.Location.interest.InterestPredicate;

import java.time.Instant;
import java.util.*;

public class ObjectStore implements DistributedManager, ChangeReceiver, ObjectLocationStorageInterface {

    private ObjectStorageReplicationInterface replicaManager;
    private ObjectStorageLocationInterface locationManager;
    private ArrayList<HashMap<GameObjectUUID, HashMap<String, GameField>>> store;
    private HashMap<GameObjectUUID, Mode> objectModes;
    private HashMap<GameObjectUUID, HashSet<String>> objectInterestingFields;

    private int bufferStart = 0;
    private ArrayList<Date> bufferLag;
    private ArrayList<RemoteChange> remoteChangeBuffer = new ArrayList<>();

    private int circ(int index) {
        return (bufferStart + index) % store.size();
    }

    public int getBufferIndex(Date now) {
        for (int i = 0; i < bufferLag.size(); i++) {
            if (bufferLag.get(circ(i)).compareTo(now) >= 0) {
                return i;
            }
        }
        return -1;
    }

    public ObjectStore(ObjectStorageReplicationInterface replicaManager, ObjectStorageLocationInterface locationManager,
                       int size) {
        this.replicaManager = replicaManager;
        this.locationManager = locationManager;
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
            /*
             * This deletes a replica from the entire buffer
             * (should not be needed, because system is trusted)
             * This deletes secondaries from the entire buffer if the primary was deleted
             * Does not delete secondaries if message was due to not being interested anymore
             */
            if (objectModes.get(change.getTarget()) == Mode.REPLICA ||
                    (objectModes.get(change.getTarget()) == Mode.SECONDARY && ((RemoteDeleteReplicaChange) change).getPrimaryDeleted())) {
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
            final RemoteFieldChange castedChange = (RemoteFieldChange) change;
            if (objectModes.get(change.getTarget()) == Mode.PRIMARY) {
                replicaManager.broadcastUpdate(change);
                if (objectInterestingFields.get(change.getTarget()).contains((castedChange.getField()))) {
                    locationManager.update(change.getTarget(), castedChange.getField(), (InterestingGameField) castedChange.getValue());
                }
            }
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
    public InterestingGameField queryOneField(GameObjectUUID id, String field) {
        GameField value = read(id, field, 0);
        if (objectInterestingFields.get(id).contains(field)) {
            return (InterestingGameField) value;
        } else {
            return null;
        }
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
                    replicaManager.broadcastUpdate(new RemoteFieldChange(fieldChange.getTarget(), fieldChange.getField(), fieldChange.getValue(), Date.from(Instant.now())));
                    if (interesting) {
                        locationManager.update(fieldChange.getTarget(), fieldChange.getField(), (InterestingGameField) fieldChange.getValue());
                    }
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
            store.get(circ(change.getBufferIndex())).remove(change.getTarget());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public GameObjectUUID create(HashMap<String, GameField> fields, HashSet<String> interesting_fields,
                                 InterestPredicate predicate, GameObjectUUID author, int bufferIndex) {
        HashMap<GameObjectUUID, HashMap<String, GameField>> state = store.get(circ(bufferIndex));
        if (author == null || objectModes.get(author) == Mode.PRIMARY) {
            GameObjectUUID uuid = GameObjectUUID.randomUUID();
            objectInterestingFields.put(uuid, interesting_fields);
            objectModes.put(uuid, Mode.PRIMARY);
            state.put(uuid, fields);
            replicaManager.createPrimary(uuid);

            HashMap<String, InterestingGameField> interesting_state = new HashMap<>();
            fields.forEach((key, value) -> {
                if (interesting_fields.contains(key)) {
                    interesting_state.put(key, (InterestingGameField) value);
                }
            });
            locationManager.add(uuid, predicate, interesting_state);
            return uuid;
        } else {
            return null;
        }
    }

    @Override
    public boolean delete(GameObjectUUID uuid, GameObjectUUID author) {
        if (objectModes.get(author) == Mode.PRIMARY) {
            for (int i = 0; i < store.size(); i++) {
                store.get(i).remove(uuid);
            }
            objectInterestingFields.remove(uuid);
            objectModes.remove(uuid);
            if (objectModes.get(uuid) == Mode.PRIMARY) {
                RemoteDeleteReplicaChange change = new RemoteDeleteReplicaChange(uuid, Date.from(Instant.now()), true);
                replicaManager.deletePrimary(uuid, change);
                locationManager.delete(uuid);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void queryInterest() {
        locationManager.queryInterest();
    }

    @Override
    public void receiveChange(RemoteChange change) {
        remoteChangeBuffer.add(change);
    }

    @Override
    public void deleteReplica(GameObjectUUID id, Date timestamp) {
        remoteChangeBuffer.add(new RemoteDeleteReplicaChange(id, Date.from(Instant.now()), false));
    }

    @Override
    public void promoteSecondary(GameObjectUUID id) {
        // TODO: Maybe send something to location?
        objectModes.put(id, Mode.PRIMARY);
    }
}