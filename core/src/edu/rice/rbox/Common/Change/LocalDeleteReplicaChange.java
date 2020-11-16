package edu.rice.rbox.Common.Change;

import edu.rice.rbox.Common.GameObjectUUID;

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

  @Override
  public LocalChange copyWithBufferIndex(int i) {
    return new LocalDeleteReplicaChange(this.getTarget(), i);
  }
}