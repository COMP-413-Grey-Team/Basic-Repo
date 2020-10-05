package edu.rice.comp413fall2020grey.Replication.Messages;

import edu.rice.comp413fall2020grey.Common.GameObjectUUID;
import edu.rice.comp413fall2020grey.Common.Message;
import edu.rice.comp413fall2020grey.Common.ServerUUID;

import java.util.Date;

/**
 * Abstract class that defines the behaviour for
 * messages related to replicas. This includes:
 *    - Subscribe
 *    - New replicas/confirm subscription
 *    - Unsubscribe
 *    - Update
 */
public abstract class ReplicaMessage extends Message {

  /**
   * ID for the GameObject subject of this message
   */
  private final GameObjectUUID targetObject;

  /**
   * Constructor for replica messages.
   * @param timestamp The time this message was sent.
   * @param originSuperpeer Unique ID for sender of message.
   * @param targetObject Unique ID for object that is the subject of the message.
   */
  protected ReplicaMessage(Date timestamp, ServerUUID originSuperpeer, GameObjectUUID targetObject) {
    super(timestamp, originSuperpeer);
    this.targetObject = targetObject;
  }

  /**
   * @return UUID of GameObject that is the subject of this message
   */
  public GameObjectUUID getTargetObject() {
    return targetObject;
  }

}
