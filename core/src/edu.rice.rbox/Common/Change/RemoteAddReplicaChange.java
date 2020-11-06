package edu.rice.rbox.Common.Change;

<<<<<<< HEAD:core/src/edu/rice/rbox/Common/Change/RemoteAddReplicaChange.java
<<<<<<< HEAD:core/src/edu/rice/rbox/Common/Change/RemoteAddReplicaChange.java
import edu.rice.rbox.Common.GameObjectUUID;
=======
=======
>>>>>>> ce7c210141f92f16e84a6a67b4ab90cc620f71bf:core/src/edu/rice/comp413fall2020grey/Common/Change/RemoteAddReplicaChange.java
import edu.rice.comp413fall2020grey.Common.GameField;
import edu.rice.comp413fall2020grey.Common.GameObjectUUID;
>>>>>>> ce7c210... getting all changes from master:core/src/edu/rice/comp413fall2020grey/Common/Change/RemoteAddReplicaChange.java

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
