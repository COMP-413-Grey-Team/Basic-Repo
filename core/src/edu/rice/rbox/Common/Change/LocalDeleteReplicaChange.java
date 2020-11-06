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
<<<<<<< HEAD
  public LocalDeleteReplicaChange copyWithIndex(int i) {
      return new LocalDeleteReplicaChange(this.getTarget(), i);
=======
  public LocalChange copyWithBufferIndex(int i) {
    return new LocalDeleteReplicaChange(this.getTarget(), i);
>>>>>>> d959a60 (merges new changes)
  }
}