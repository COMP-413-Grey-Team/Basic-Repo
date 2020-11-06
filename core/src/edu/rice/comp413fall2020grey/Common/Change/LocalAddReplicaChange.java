package edu.rice.comp413fall2020grey.Common.Change;

import edu.rice.comp413fall2020grey.Common.GameField;
import edu.rice.comp413fall2020grey.Common.GameObjectUUID;

import java.util.HashMap;

public class LocalAddReplicaChange extends AddReplicaChange implements LocalChange {

  private final int bufferIndex;

  public LocalAddReplicaChange(GameObjectUUID target, HashMap<String, GameField> object, int bufferIndex) {
    super(target, object);
    this.bufferIndex = bufferIndex;
  }

  @Override
  public int getBufferIndex() {
    return bufferIndex;
  }

  @Override
  public LocalAddReplicaChange copyWithIndex(int i) {
    return new LocalAddReplicaChange(this.getTarget(), this.getObject(), i);
  }
}
