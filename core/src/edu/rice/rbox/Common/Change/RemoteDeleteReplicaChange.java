package edu.rice.rbox.Common.Change;

import edu.rice.rbox.Common.GameObjectUUID;

import java.util.Date;

public class RemoteDeleteReplicaChange extends DeleteReplicaChange implements RemoteChange {
  private final Date timestamp;
  private final boolean primaryDeleted;

  public RemoteDeleteReplicaChange(GameObjectUUID target, Date timestamp, boolean primaryDeleted) {
    super(target);
    this.timestamp = timestamp;
    this.primaryDeleted = primaryDeleted;
  }

  @Override
  public Date getTimestamp() {
    return timestamp;
  }

  public boolean getPrimaryDeleted() {
    return primaryDeleted;
  }
}