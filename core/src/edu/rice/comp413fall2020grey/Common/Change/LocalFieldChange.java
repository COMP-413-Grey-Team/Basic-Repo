package edu.rice.comp413fall2020grey.Common.Change;

import edu.rice.comp413fall2020grey.Common.Change.Change;
import edu.rice.comp413fall2020grey.Common.GameField;
import edu.rice.comp413fall2020grey.Common.GameObjectUUID;
import edu.rice.comp413fall2020grey.Common.GameField;

public class LocalFieldChange extends FieldChange implements LocalChange {

  private final int bufferIndex;

  public LocalFieldChange(GameObjectUUID target, String field, GameField value, int bufferIndex) {
    super(target, field, value);
    this.bufferIndex = bufferIndex;
  }

  public int getBufferIndex() {
    return bufferIndex;
  }

  @Override
  public LocalFieldChange copyWithIndex(int i) {
    return new LocalFieldChange(this.getTarget(), this.getField(), this.getValue(), i);
  }

}
