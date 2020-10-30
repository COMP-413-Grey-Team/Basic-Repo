package edu.rice.comp413fall2020grey.ObjStorage;

import edu.rice.comp413fall2020grey.Common.Change.RemoteChange;
import edu.rice.comp413fall2020grey.Common.GameObjectUUID;

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
  void updatePrimary(RemoteChange change, Boolean interesting);
  void broadcastUpdate(RemoteChange change, Boolean interesting);

  void createPrimary(GameObjectUUID id, HashMap<String, Serializable> interestingFields);
  void deletePrimary(GameObjectUUID id);
}
