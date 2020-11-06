package edu.rice.rbox.Common.Change;

import edu.rice.rbox.Common.GameObjectUUID;

import java.util.Date;

public class RemoteDeleteReplicaChange extends DeleteReplicaChange implements RemoteChange {
  private final Date timestamp;

  public RemoteDeleteReplicaChange(GameObjectUUID target, Date timestamp) {
    super(target);
    this.timestamp = timestamp;
  }

  @Override
  public Date getTimestamp() {
    return timestamp;
  }
}