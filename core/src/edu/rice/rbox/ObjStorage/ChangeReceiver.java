package edu.rice.rbox.ObjStorage;

import edu.rice.rbox.Common.Change.RemoteChange;
import edu.rice.rbox.Common.Change.RemoteDeleteReplicaChange;
import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Common.Mode;

import java.time.Instant;
import java.util.Date;

public interface ChangeReceiver {

  void receiveChange(RemoteChange change);
  RemoteChange getReplica(GameObjectUUID id);
  void deleteReplica(GameObjectUUID id, Date timestamp);
  void promoteSecondary(GameObjectUUID id);
}
