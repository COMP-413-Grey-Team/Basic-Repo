package edu.rice.comp413fall2020grey.Common.Change;

import edu.rice.comp413fall2020grey.Common.GameObjectUUID;

import java.io.Serializable;

public abstract class FieldChange extends Change {

  private final String field;
  private final Serializable value;

  public FieldChange(GameObjectUUID target, String field, Serializable value) {
    super(target);
    this.field = field;
    this.value = value;
  }

  public String getField() {
    return field;
  }

  public Serializable getValue() {
    return value;
  }

}
