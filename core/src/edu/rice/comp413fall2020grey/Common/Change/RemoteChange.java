package edu.rice.comp413fall2020grey.Common.Change;

import edu.rice.comp413fall2020grey.Common.GameObjectUUID;

import java.util.Date;

public interface RemoteChange {
  Date getTimestamp();
  GameObjectUUID getTarget();
}
