package edu.rice.rbox.Common.Change;

import edu.rice.rbox.Common.GameObjectUUID;

import java.util.Date;

public interface RemoteChange {
  Date getTimestamp();
  GameObjectUUID getTarget();
}
