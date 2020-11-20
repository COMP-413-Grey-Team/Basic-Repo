package edu.rice.rbox.Common.Change;

import edu.rice.rbox.Common.GameField.GameField;
import edu.rice.rbox.Common.GameObjectUUID;

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
