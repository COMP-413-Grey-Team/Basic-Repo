package edu.rice.rbox.ObjStorage;

<<<<<<< HEAD:core/src/edu/rice/rbox/ObjStorage/ObjectStorageReplicationInterface.java
import edu.rice.rbox.Common.Change.RemoteChange;
import edu.rice.rbox.Common.GameObjectUUID;
=======
import edu.rice.comp413fall2020grey.Common.Change.RemoteChange;
import edu.rice.comp413fall2020grey.Common.GameField;
import edu.rice.comp413fall2020grey.Common.GameObjectUUID;
>>>>>>> ce7c210... getting all changes from master:core/src/edu/rice/comp413fall2020grey/ObjStorage/ObjectStorageReplicationInterface.java

import java.io.Serializable;
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
