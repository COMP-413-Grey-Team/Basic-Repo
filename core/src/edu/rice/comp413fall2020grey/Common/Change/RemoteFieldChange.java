package edu.rice.comp413fall2020grey.Common.Change;

import edu.rice.comp413fall2020grey.Common.GameField;
import edu.rice.comp413fall2020grey.Common.GameObjectUUID;
import edu.rice.comp413fall2020grey.Common.GameField;
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
