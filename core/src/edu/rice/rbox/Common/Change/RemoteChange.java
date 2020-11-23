package edu.rice.rbox.Common.Change;

import edu.rice.rbox.Common.GameObjectUUID;

import java.io.Serializable;
import java.util.Date;

public interface RemoteChange extends Serializable {
  Date getTimestamp();
  GameObjectUUID getTarget();
}
