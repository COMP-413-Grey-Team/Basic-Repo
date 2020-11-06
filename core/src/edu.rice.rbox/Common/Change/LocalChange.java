package edu.rice.rbox.Common.Change;

import edu.rice.rbox.Common.GameObjectUUID;

public interface LocalChange {

  int getBufferIndex();
  GameObjectUUID getTarget();

}
