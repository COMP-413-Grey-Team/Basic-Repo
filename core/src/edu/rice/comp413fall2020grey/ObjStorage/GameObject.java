package edu.rice.comp413fall2020grey.ObjStorage;

/**
 * Represents an object in a game with an interest function.
 */
public interface GameObject {

  /**
   * Metadata for this object that will be stored on the registrar and used by object location.
   */
  GameObjectMetadata getMetadata();

  /**
   * Returns if a given object is interesting to the caller, and should be loaded on its superpeer.
   */
  boolean isInterestedIn(GameObjectMetadata metadata);

}