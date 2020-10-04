package edu.rice.comp413fall2020grey.Replication.Messages;

import edu.rice.comp413fall2020grey.Common.GameObject;

import java.util.Date;
import java.util.UUID;

/**
 * Message for subscriptions. The target object is the primary
 * this superpeer would like to subscribe to and receive updates about.
 */
public class SubscribeMessage extends ReplicaMessage {

  private final GameObject newReplica;

  protected SubscribeMessage(Date timestamp, UUID originSuperpeer, UUID targetObject, GameObject newReplica) {
    super(timestamp, originSuperpeer, targetObject);
    this.newReplica = newReplica;
  }

  public GameObject getNewReplica() { return this.newReplica; }

}
