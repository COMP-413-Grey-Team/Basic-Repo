package edu.rice.comp413fall2020grey.Replication.Messages;

import edu.rice.comp413fall2020grey.Common.GameObjectUUID;
import edu.rice.comp413fall2020grey.Common.ServerUUID;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

/**
 * Message for object updates. May be sent from either a replica or
 * a primary object. The target object is the target for the changes.
 */
public class UpdateMessage extends ReplicaMessage {

  /**
   * A string representing the field being changed.
   */
  private final String field;

  /**
   * A new value for the field being changed.
   */
  private final Serializable value;

  /**
   * Constructor for update message.
   * @param timestamp The time this message was sent.
   * @param originSuperpeer Unique ID of message sender.
   * @param targetObject Unique ID for target of the update changes.
   * @param field The field of targetObject being changed.
   */
  protected UpdateMessage(Date timestamp,
                          ServerUUID originSuperpeer,
                          GameObjectUUID targetObject,
                          String field,
                          Serializable value) {
    super(timestamp, originSuperpeer, targetObject, "update");
    this.field = field;
    this.value = value;
  }

  /**
   * @return A string representing this object's changed field.
   */
  public String getField() {
    return this.field;
  }

  /**
   * @return The value for the changed field.
   */
  public Serializable getValue() {
    return value;
  }
}
