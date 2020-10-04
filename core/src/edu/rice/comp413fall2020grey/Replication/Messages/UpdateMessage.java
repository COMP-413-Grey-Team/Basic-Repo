package edu.rice.comp413fall2020grey.Replication.Messages;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

/**
 * Message for object updates. May be sent from either a replica or
 * a primary object. The target object is the target for the changes.
 */
public class UpdateMessage extends ReplicaMessage {

  /**
   * Authorizing object for update.
   */
  private final UUID authorObject;

  /**
   * A set of strings representing object updates.
   */
  private final Set<String> changes;

  protected UpdateMessage(Date timestamp,
                          UUID originSuperpeer,
                          UUID targetObject,
                          UUID authorObject,
                          Set<String> changes) {
    super(timestamp, originSuperpeer, targetObject);
    this.authorObject = authorObject;
    this.changes = changes;
  }

  /**
   * @return UUID of the object authorizing this change.
   */
  public UUID getAuthorObject() {
    return this.authorObject;
  }

  /**
   * @return A set of changes for this object.
   * Changes are in the format of Field: Value, where
   * Field is a GameObject's changed field and
   * Value is the new value associated with that field.
   */
  public Set<String> getChanges() {
    return this.changes;
  }
}
