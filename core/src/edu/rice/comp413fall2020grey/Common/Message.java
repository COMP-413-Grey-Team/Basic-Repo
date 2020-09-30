package edu.rice.comp413fall2020grey.Common;

import java.util.Date;
import java.util.UUID;

/**
 * Interface that defines abstract message behaviour.
 */
public interface Message {

  /**
   * @return Date timestamp when message was created and sent
   */
  Date getTimestamp();

  /**
   * @return UUID of superpeer sender of this message
   */
  UUID getOriginSuperpeer();

}

/**
 * Interface that defines abstract behaviour for
 * messages related to replicas. This includes:
 *    - Subscrbe
 *    - New replicas/confirm subscription
 *    - Unsubscribe
 *    - Update
 */
public interface ReplicaMessage implements Message {

  /**
   * @return UUID of GameObject that is the subject of this message
   */
  UUID getTargetObject();

}

/**
 * Interface for subscription messages. The target object is the primary
 * this superpeer would like to subscribe to and recieve updates about.
 */
public interface Subscribe implements ReplicaMessage {
}

/**
 * Interface for subscription confirmation messages. The target object is
 * the primary this superpeer has sucessfully subscribed to.
 */
public interface ConfirmSubscription implements ReplicaMessage {

  /**
   * @return The new replica of the target object.
   * Note: Temporary return type -- Replication should define what this object returns.
   */
  GameObject getNewReplica();

}

/**
 * Interface for update messages. May be sent from either a replica or
 * a primary object. The target object is the target for the changes.
 */
public interface Update implements ReplicaMessage {

  /**
   * @return UUID of the object authorizing this change.
   */
  UUID getAuthorObject();

  /**
   * @return A set of changes for this object.
   * Changes are in the format of Field: Value, where
   * Field is a GameObject's changed field and
   * Value is the new value associated with that field.
   */
  Set<String> getChanges();

}
