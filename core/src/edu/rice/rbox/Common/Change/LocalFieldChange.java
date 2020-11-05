package edu.rice.rbox.Common.Change;

import edu.rice.rbox.Common.GameObjectUUID;
import java.io.Serializable;

public class LocalFieldChange extends FieldChange implements LocalChange {

  private final int bufferIndex;

  public LocalFieldChange(GameObjectUUID target, String field, Serializable value, int bufferIndex) {
    super(target, field, value);
    this.bufferIndex = bufferIndex;
  }

  public int getBufferIndex() {
    return bufferIndex;
  }

}
