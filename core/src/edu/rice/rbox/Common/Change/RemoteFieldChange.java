package edu.rice.rbox.Common.Change;

import edu.rice.rbox.Common.GameObjectUUID;
import java.io.Serializable;
import java.util.Date;

public class RemoteFieldChange extends FieldChange implements RemoteChange {

  private final Date timestamp;
  public RemoteFieldChange(GameObjectUUID target,
                           String field,
                           Serializable value,
                           Date timestamp) {
    super(target, field, value);
    this.timestamp = timestamp;
  }

  public Date getTimestamp() {
    return timestamp;
  }

}
