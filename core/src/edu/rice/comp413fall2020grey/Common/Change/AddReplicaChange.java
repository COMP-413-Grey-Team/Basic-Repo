package edu.rice.comp413fall2020grey.Common.Change;

import edu.rice.comp413fall2020grey.Common.GameField;
import edu.rice.comp413fall2020grey.Common.GameObjectUUID;

import java.io.Serializable;
import java.util.HashMap;

public class AddReplicaChange extends Change {

  private HashMap<String, GameField> object;

  public AddReplicaChange(GameObjectUUID target, HashMap<String, GameField> object) {
    super(target);
    this.object = object;
  }

  public HashMap<String, GameField> getObject() {
    return object;
  }
}
