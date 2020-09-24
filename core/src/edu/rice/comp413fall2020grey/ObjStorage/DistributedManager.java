package edu.rice.comp413fall2020grey.ObjStorage;

import java.util.UUID;

/**
 * This will the the interface exposed to the Game Server to interact with the distributed system (specifically, with Object Storage).
 */
public interface DistributedManager {

  /**
   * Updates all objects in storage according to most recent cache and lag compensation technique
   */
  void synchronize();

  /**
   * Advances the buffer for all objects in the store
   * This deletes all the oldest buffer entries
   */
  // TODO: we need additional documentation about how the server should be using this buffer.
  void advanceBuffer();

  /**
   * Returns object from storage using some provided object information (relevant keys to be decided)
   * This method can be used at game server/game startup to obtain all the necessary objects to begin rendering the game on the client
   */
  GameObject read(final UUID gameObjectID, final int bufferIndex);

  /**
   * Sends request from authorObject to change the state of targetObject – state changes reflected in local non-canonical cache.
   * @return if the write was accepted
   */
  boolean write(GameObject target, int bufferIndex, GameObject author, String updateMsg);
}
