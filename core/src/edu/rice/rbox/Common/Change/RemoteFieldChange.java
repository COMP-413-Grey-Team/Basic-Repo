package edu.rice.rbox.Common.Change;

<<<<<<< HEAD:core/src/edu/rice/rbox/Common/Change/RemoteFieldChange.java
import edu.rice.rbox.Common.GameObjectUUID;
import java.io.Serializable;
=======
import edu.rice.comp413fall2020grey.Common.GameField;
import edu.rice.comp413fall2020grey.Common.GameObjectUUID;
import edu.rice.comp413fall2020grey.Common.GameField;
>>>>>>> ce7c210... getting all changes from master:core/src/edu/rice/comp413fall2020grey/Common/Change/RemoteFieldChange.java
import java.util.Date;

public class RemoteFieldChange extends FieldChange implements RemoteChange {

  private final Date timestamp;
  public RemoteFieldChange(GameObjectUUID target,
                           String field,
                           GameField value,
                           Date timestamp) {
    super(target, field, value);
    this.timestamp = timestamp;
  }

  public Date getTimestamp() {
    return timestamp;
  }

}
