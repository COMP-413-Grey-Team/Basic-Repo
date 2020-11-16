package edu.rice.rbox.ObjStorage;

import edu.rice.rbox.Common.Change.RemoteChange;
import edu.rice.rbox.Common.GameField;
import edu.rice.rbox.Common.GameObjectUUID;

import java.util.HashMap;

/**
 * The interface implemented by Location that will be used by Object Storage
 */
public interface ObjectStorageLocationInterface {

  /**
   * Notifies replication .
   */
  void updatePrimary(RemoteChange change);


  /**
   * Notifies replication that a new primary has been created and should notify the registrar.
   */
  void createPrimary(GameObjectUUID id, HashMap<String, GameField> interestingField, String predicate);

  /**
   * Notifies replication that a new primary has been created and should notify the registrar and all the replicas.
   */
  void deletePrimary(GameObjectUUID id, RemoteChange change);
}
