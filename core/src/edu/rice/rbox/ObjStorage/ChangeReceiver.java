package edu.rice.rbox.ObjStorage;

import edu.rice.rbox.Common.Change.RemoteChange;
import edu.rice.rbox.Common.GameObjectUUID;

public interface ChangeReceiver {

  void receiveChange(RemoteChange change);
  RemoteChange getReplica(GameObjectUUID id);
}
