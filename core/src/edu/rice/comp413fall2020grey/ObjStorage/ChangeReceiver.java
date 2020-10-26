package edu.rice.comp413fall2020grey.ObjStorage;

import edu.rice.comp413fall2020grey.Common.Change.Change;
import edu.rice.comp413fall2020grey.Common.Change.RemoteChange;

public interface ChangeReceiver {

  void receiveChange(RemoteChange change);

}
