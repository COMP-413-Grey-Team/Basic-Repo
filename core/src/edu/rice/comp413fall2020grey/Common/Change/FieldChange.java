package edu.rice.comp413fall2020grey.Common.Change;

import edu.rice.comp413fall2020grey.Common.GameField;
import edu.rice.comp413fall2020grey.Common.GameObjectUUID;
import edu.rice.comp413fall2020grey.Common.GameField;

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
