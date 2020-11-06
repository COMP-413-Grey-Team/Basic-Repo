package edu.rice.rbox.Common.Change;

<<<<<<< HEAD:core/src/edu/rice/rbox/Common/Change/LocalFieldChange.java
import edu.rice.rbox.Common.GameObjectUUID;
import java.io.Serializable;
=======
import edu.rice.comp413fall2020grey.Common.Change.Change;
import edu.rice.comp413fall2020grey.Common.GameField;
import edu.rice.comp413fall2020grey.Common.GameObjectUUID;
import edu.rice.comp413fall2020grey.Common.GameField;
>>>>>>> ce7c210... getting all changes from master:core/src/edu/rice/comp413fall2020grey/Common/Change/LocalFieldChange.java

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
