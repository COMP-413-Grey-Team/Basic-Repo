package edu.rice.comp413fall2020grey.Replication.Messages;

import edu.rice.comp413fall2020grey.Common.Message;

import java.util.Date;
import java.util.UUID;

/**
 * Abstract class that defines the behaviour for
 * messages related to replicas. This includes:
 *    - Subscribe
 *    - New replicas/confirm subscription
 *    - Unsubscribe
 *    - Update
 */
public abstract class ReplicaMessage extends Message {

  private final UUID targetObject;

  protected ReplicaMessage(Date timestamp, UUID originSuperpeer, UUID targetObject) {
    super(timestamp, originSuperpeer);
    this.targetObject = targetObject;
  }

  /**
   * @return UUID of GameObject that is the subject of this message
   */
  public UUID getTargetObject() {
    return targetObject;
  }

}
