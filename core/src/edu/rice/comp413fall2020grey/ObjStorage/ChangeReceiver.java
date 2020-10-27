package edu.rice.comp413fall2020grey.ObjStorage;

import edu.rice.comp413fall2020grey.Common.Change.Change;
import edu.rice.comp413fall2020grey.Common.Change.RemoteChange;
import edu.rice.comp413fall2020grey.Common.GameObjectUUID;

public interface ChangeReceiver {

  void receiveChange(RemoteChange change);
  RemoteChange getReplica(GameObjectUUID id);
}
