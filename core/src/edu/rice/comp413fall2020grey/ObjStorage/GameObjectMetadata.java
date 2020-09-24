package edu.rice.comp413fall2020grey.ObjStorage;

/**
 * Metadata for this object that will be stored on the registrar and used by object location.
 */
public interface GameObjectMetadata {

  /**
   * Returns the type of the game object this metadata is for. This will help determine if this object is interesting to other objects.
   */
  Class<?> getUnderlyingType();

}
