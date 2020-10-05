package edu.rice.comp413fall2020grey.Replication.Messages;

import edu.rice.comp413fall2020grey.Common.GameObject;
import edu.rice.comp413fall2020grey.Common.GameObjectUUID;
import edu.rice.comp413fall2020grey.Common.ServerUUID;

import java.util.Date;

/**
 * Message for subscription confirmations. The target object is
 * the primary object this superpeer has successfully subscribed to.
 */
public class ConfirmSubscriptionMessage extends ReplicaMessage {

  /**
   * New replica to add to storage.
   */
  private final GameObject newReplica;

  /**
   * Constructor for subscription confirmation message.
   * @param timestamp The time this message was sent.
   * @param originSuperpeer Unique ID for the sender of this message.
   * @param targetObject Unique ID for object this superpeer successfully subscribed to.
   * @param newReplica The new replica of targetObject for this superpeer to store.
   */
  protected ConfirmSubscriptionMessage(Date timestamp,
                                       ServerUUID originSuperpeer,
                                       GameObjectUUID targetObject,
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
