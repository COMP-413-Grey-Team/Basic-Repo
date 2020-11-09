package edu.rice.rbox.Replication.Messages;

<<<<<<< HEAD:core/src/edu/rice/rbox/Replication/Messages/UpdateMessage.java
<<<<<<< HEAD:core/src/edu/rice/rbox/Replication/Messages/UpdateMessage.java
import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Common.ServerUUID;
=======
=======
>>>>>>> ce7c210141f92f16e84a6a67b4ab90cc620f71bf:core/src/edu/rice/comp413fall2020grey/Replication/Messages/UpdateMessage.java
import edu.rice.comp413fall2020grey.Common.GameField;
import edu.rice.comp413fall2020grey.Common.GameObjectUUID;
import edu.rice.comp413fall2020grey.Common.ServerUUID;
>>>>>>> ce7c210... getting all changes from master:core/src/edu/rice/comp413fall2020grey/Replication/Messages/UpdateMessage.java

import java.io.Serializable;
import java.util.Date;

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
  private final GameField value;

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
                          GameField value) {
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
  public GameField getValue() {
    return value;
  }
}
