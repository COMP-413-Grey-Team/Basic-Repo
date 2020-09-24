package edu.rice.comp413fall2020grey.ObjStorage;

import edu.rice.comp413fall2020grey.Common.GameObject;
import edu.rice.comp413fall2020grey.Common.Message;

import java.util.Set;

/**
 * The interface implemented by Replication that will be used by Object Storage
 */
// TODO: better name
public interface ObjectStorageReplicationInterface {

  /**
   * Returns every message that Replication has received since this function was last called.
   */
  Set<Message> flushCache();

  /**
   * If gameObj is a primary, then this function sends msg to every replica of gameObj on other superpeers.
   * If gameObj is a replica, then this function sends msg to its primary.
   */
  void sendUpdate(GameObject gameObj, Message msg);
}
