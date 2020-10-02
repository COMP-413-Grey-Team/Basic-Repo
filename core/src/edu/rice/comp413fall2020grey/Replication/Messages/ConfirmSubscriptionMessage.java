package edu.rice.comp413fall2020grey.Replication.Messages;

import edu.rice.comp413fall2020grey.Common.GameObject;

import java.util.Date;
import java.util.UUID;

/**
 * Message for subscription confirmations. The target object is
 * the primary object this superpeer has successfully subscribed to.
 */
public class ConfirmSubscriptionMessage extends ReplicaMessage {

  private final GameObject newReplica;

  protected ConfirmSubscriptionMessage(Date timestamp,
                                       UUID originSuperpeer,
                                       UUID targetObject,
                                       GameObject newReplica) {
    super(timestamp, originSuperpeer, targetObject);
    this.newReplica = newReplica;
  }

  /**
   * @return The new replica of the target object.
   * Note: Temporary return type -- Replication should define what this object returns.
   */
  public GameObject getNewReplica() {
    return newReplica;
  }
}
