package edu.rice.rbox.Common.Change;

import edu.rice.rbox.Common.GameField;
import edu.rice.rbox.Common.GameObjectUUID;

public class LocalFieldChange extends FieldChange implements LocalChange {

  private final int bufferIndex;

  public LocalFieldChange(GameObjectUUID target, String field, GameField value, int bufferIndex) {
    super(target, field, value);
    this.bufferIndex = bufferIndex;
  }

  public int getBufferIndex() {
    return bufferIndex;
  }

  public LocalFieldChange copyWithIndex(int i) {
    return new LocalFieldChange(this.getTarget(), this.getField(), this.getValue(), i);
  }

}
