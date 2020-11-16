package edu.rice.rbox.ObjStorage;

import edu.rice.rbox.Common.Change.RemoteChange;
import edu.rice.rbox.Common.GameField.GameField;
import edu.rice.rbox.Common.GameObjectUUID;

import java.util.HashMap;

/**
 * The interface implemented by Replication that will be used by Object Storage
 */
// TODO: better name
public interface ObjectStorageReplicationInterface {

  /**
   * If gameObj is a primary, then this function sends msg to every replica of gameObj on other superpeers.
   * If gameObj is a replica, then this function sends msg to its primary.
   */
  void updatePrimary(RemoteChange change);

  /**
   * Send an update to all of the replicas.
   * @param interesting Whether the change is interesting and should be sent to the registrar.
   */
  void broadcastUpdate(RemoteChange change, Boolean interesting);

  /**
   * Notifies replication that a new primary has been created and should notify the registrar.
   */
  void createPrimary(GameObjectUUID id, HashMap<String, GameField> interestingField, String predicate);

  /**
   * Notifies replication that a new primary has been created and should notify the registrar and all the replicas.
   */
  void deletePrimary(GameObjectUUID id, RemoteChange change);
}
