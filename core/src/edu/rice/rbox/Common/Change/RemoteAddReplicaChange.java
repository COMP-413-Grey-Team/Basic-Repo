package edu.rice.rbox.Common.Change;

<<<<<<< HEAD
import edu.rice.rbox.Common.GameField.GameField;
=======
import edu.rice.rbox.Common.GameField;
>>>>>>> d959a60 (merges new changes)
import edu.rice.rbox.Common.GameObjectUUID;

import java.util.Date;
import java.util.HashMap;

public class RemoteAddReplicaChange extends AddReplicaChange implements RemoteChange {
  private final Date timestamp;

  public RemoteAddReplicaChange(GameObjectUUID target, HashMap<String, GameField> object, Date timestamp) {
    super(target, object);
    this.timestamp = timestamp;
  }

  @Override
  public Date getTimestamp() {
    return timestamp;
  }
}
