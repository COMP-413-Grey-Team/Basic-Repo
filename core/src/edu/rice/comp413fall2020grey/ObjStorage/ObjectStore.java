package edu.rice.comp413fall2020grey.ObjStorage;

import edu.rice.comp413fall2020grey.Common.GameObject;
import edu.rice.comp413fall2020grey.Common.GameObjectMetadata;
import edu.rice.comp413fall2020grey.Common.GameObjectUUID;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class ObjectStore implements DistributedManager{

    ArrayList<HashMap<GameObjectUUID, HashMap<String, Serializable>>> store;
    HashMap<GameObjectUUID, GameObjectMetadata> metadata;

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
            case PRIMARY:
                store.get(change.bufferIndex).get(change.target).put(change.field, change.value);
                //asdf.sendUpdate(change.target, change.field, change.value);
                return true;
            case REPLICA:
            case SECONDARY:
                switch (metadata.get(change.target).getMode()) {
                    case PRIMARY:
                        return false;
                    case REPLICA:
                    case SECONDARY:
                        store.get(change.bufferIndex).get(change.target).put(change.field, change.value);
                        return true;
                }
        }
    }
}
