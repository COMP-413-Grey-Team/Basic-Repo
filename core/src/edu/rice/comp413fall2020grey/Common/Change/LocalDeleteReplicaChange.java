package edu.rice.comp413fall2020grey.Common.Change;

import edu.rice.comp413fall2020grey.Common.GameObjectUUID;

public class LocalDeleteReplicaChange extends DeleteReplicaChange implements LocalChange {

  private final int bufferIndex;

  public LocalDeleteReplicaChange(GameObjectUUID target, int bufferIndex) {
    super(target);
    this.bufferIndex = bufferIndex;
  }

  @Override
  public int getBufferIndex() {
    return bufferIndex;
  }
}