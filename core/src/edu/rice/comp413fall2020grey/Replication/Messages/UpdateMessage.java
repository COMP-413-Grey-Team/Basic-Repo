package edu.rice.comp413fall2020grey.Replication.Messages;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class UpdateMessage extends ReplicaMessage {

  private final UUID authorObject;
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
    return authorObject;
  }

  /**
   * @return A set of changes for this object.
   * Changes are in the format of Field: Value, where
   * Field is a GameObject's changed field and
   * Value is the new value associated with that field.
   */
  public Set<String> getChanges() {
    return changes;
  }
}
