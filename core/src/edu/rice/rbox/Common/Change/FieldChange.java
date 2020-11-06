package edu.rice.rbox.Common.Change;

<<<<<<< HEAD:core/src/edu/rice/rbox/Common/Change/FieldChange.java
import edu.rice.rbox.Common.GameObjectUUID;

import java.io.Serializable;
=======
import edu.rice.comp413fall2020grey.Common.GameField;
import edu.rice.comp413fall2020grey.Common.GameObjectUUID;
import edu.rice.comp413fall2020grey.Common.GameField;
>>>>>>> ce7c210... getting all changes from master:core/src/edu/rice/comp413fall2020grey/Common/Change/FieldChange.java

public abstract class FieldChange extends Change {

  private final String field;
  private final GameField value;

  public FieldChange(GameObjectUUID target, String field, GameField value) {
    super(target);
    this.field = field;
    this.value = value;
  }

  public String getField() {
    return field;
  }

  public GameField getValue() {
    return value;
  }

}
