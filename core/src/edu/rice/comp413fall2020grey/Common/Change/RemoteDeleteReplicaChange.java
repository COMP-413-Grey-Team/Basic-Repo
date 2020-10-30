package edu.rice.comp413fall2020grey.Common.Change;

import edu.rice.comp413fall2020grey.Common.GameObjectUUID;

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