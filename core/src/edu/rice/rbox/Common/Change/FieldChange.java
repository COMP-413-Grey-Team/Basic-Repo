package edu.rice.rbox.Common.Change;

import edu.rice.rbox.Common.GameField;
import edu.rice.rbox.Common.GameObjectUUID;


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
