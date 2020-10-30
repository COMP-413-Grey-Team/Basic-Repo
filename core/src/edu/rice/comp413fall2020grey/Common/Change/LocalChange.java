package edu.rice.comp413fall2020grey.Common.Change;

import edu.rice.comp413fall2020grey.Common.GameObjectUUID;

public interface LocalChange {

  int getBufferIndex();
  GameObjectUUID getTarget();

}
