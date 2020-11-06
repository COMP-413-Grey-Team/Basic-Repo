package edu.rice.rbox.Common.Change;

<<<<<<< HEAD:core/src/edu/rice/rbox/Common/Change/AddReplicaChange.java
import edu.rice.rbox.Common.GameObjectUUID;
=======
import edu.rice.comp413fall2020grey.Common.GameField;
import edu.rice.comp413fall2020grey.Common.GameObjectUUID;
>>>>>>> ce7c210... getting all changes from master:core/src/edu/rice/comp413fall2020grey/Common/Change/AddReplicaChange.java

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
