package edu.rice.rbox.ObjStorage;

import edu.rice.rbox.Common.Change.RemoteChange;
import edu.rice.rbox.Common.GameObjectUUID;

import java.util.Date;

/**
 * Interface from object replication to object storage.
 */
public interface ChangeReceiver {
  /**
   * Receives a change and updates the store.
   */
  void receiveChange(RemoteChange change);

  /**
   * Deletes the specified replica.
   * Right now it deletes it from the entire buffer, this might need to change.
   * TODO: Discuss this.
   */
  void deleteReplica(GameObjectUUID id, Date timestamp);

  /**
   * Returns a replica of the specified object.
   */
  RemoteChange getReplica(GameObjectUUID id);
  void promoteSecondary(GameObjectUUID id);
}
