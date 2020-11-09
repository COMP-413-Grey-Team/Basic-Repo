package edu.rice.rbox.Common.Change;

<<<<<<< HEAD:core/src/edu/rice/rbox/Common/Change/LocalAddReplicaChange.java
import edu.rice.rbox.Common.GameObjectUUID;
=======
import edu.rice.comp413fall2020grey.Common.GameField;
import edu.rice.comp413fall2020grey.Common.GameObjectUUID;
>>>>>>> ce7c210... getting all changes from master:core/src/edu/rice/comp413fall2020grey/Common/Change/LocalAddReplicaChange.java

import java.util.HashMap;

public class LocalAddReplicaChange extends AddReplicaChange implements LocalChange {

  private final int bufferIndex;

  public LocalAddReplicaChange(GameObjectUUID target, HashMap<String, GameField> object, int bufferIndex) {
    super(target, object);
    this.bufferIndex = bufferIndex;
  }

  @Override
  public int getBufferIndex() {
    return bufferIndex;
  }

  @Override
  public LocalAddReplicaChange copyWithIndex(int i) {
    return new LocalAddReplicaChange(this.getTarget(), this.getObject(), i);
  }
}
