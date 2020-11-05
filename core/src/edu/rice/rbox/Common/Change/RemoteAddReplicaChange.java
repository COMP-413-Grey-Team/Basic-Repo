package edu.rice.rbox.Common.Change;

import edu.rice.rbox.Common.GameObjectUUID;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

public class RemoteAddReplicaChange extends AddReplicaChange implements RemoteChange {
  private final Date timestamp;

  public RemoteAddReplicaChange(GameObjectUUID target, HashMap<String, Serializable> object, Date timestamp) {
    super(target, object);
    this.timestamp = timestamp;
  }

  @Override
  public Date getTimestamp() {
    return timestamp;
  }
}
