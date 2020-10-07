package edu.rice.comp413fall2020grey.Common.Change;

import edu.rice.comp413fall2020grey.Common.Change.Change;
import edu.rice.comp413fall2020grey.Common.GameObjectUUID;
import java.io.Serializable;

public class LocalChange extends Change {

  private final int bufferIndex;

  public LocalChange(GameObjectUUID target, String field, Serializable value, int bufferIndex) {
    super(target, field, value);
    this.bufferIndex = bufferIndex;
  }

  public int getBufferIndex() {
    return bufferIndex;
  }

}
