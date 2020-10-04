package edu.rice.comp413fall2020grey.Replication.Messages;

import edu.rice.comp413fall2020grey.Common.GameObject;
import edu.rice.comp413fall2020grey.Common.GameObjectUUID;
import edu.rice.comp413fall2020grey.Common.ServerUUID;

import java.util.Date;

/**
 * Message for subscriptions. The target object is the primary
 * this superpeer would like to subscribe to and receive updates about.
 */
public class SubscribeMessage extends ReplicaMessage {

  private final GameObject newReplica;

  protected SubscribeMessage(Date timestamp, ServerUUID originSuperpeer, GameObjectUUID targetObject, GameObject newReplica) {
    super(timestamp, originSuperpeer, targetObject);
    this.newReplica = newReplica;
  }

  public GameObject getNewReplica() { return this.newReplica; }

}
