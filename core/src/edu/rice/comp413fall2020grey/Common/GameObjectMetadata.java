package edu.rice.comp413fall2020grey.Common;

import java.util.UUID;

/**
 * Metadata for this object that will be stored on the registrar and used by object location.
 */
public interface GameObjectMetadata {

  /**
   * Unique identifier for this game object across the entire distributed system.
   */
  UUID getUUID();

  /**
   *  returns if the game object is primary, replica, or secondary
   */
  Mode getMode();

  /**
   * Returns the type of the game object this metadata is for. This will help determine if this object is interesting to other objects.
   */
  Class<?> getUnderlyingType();

}
