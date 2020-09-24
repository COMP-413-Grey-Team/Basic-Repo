package edu.rice.comp413fall2020grey.ObjStorage;

public interface GameObject {

  /**
   * Returns the underlying type of this game object so the client can use it more easily.
   */
  Class<?> type();

}
