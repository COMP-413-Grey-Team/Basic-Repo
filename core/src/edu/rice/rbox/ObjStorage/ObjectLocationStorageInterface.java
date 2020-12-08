package edu.rice.rbox.ObjStorage;

import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Common.GameField.InterestingGameField;

/**
 * Interface from object replication to object storage.
 */
public interface ObjectLocationStorageInterface {
    InterestingGameField queryOneField(GameObjectUUID id, String field);
}
