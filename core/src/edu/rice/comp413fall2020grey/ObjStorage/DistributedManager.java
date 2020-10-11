package edu.rice.comp413fall2020grey.ObjStorage;

import edu.rice.comp413fall2020grey.Common.Change.Change;
import edu.rice.comp413fall2020grey.Common.Change.RemoteChange;
import edu.rice.comp413fall2020grey.Common.GameObject;
import edu.rice.comp413fall2020grey.Common.GameObjectUUID;

import edu.rice.comp413fall2020grey.Common.Change.LocalChange;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

/**
 * The interface exposed to the Game Server to interact with the distributed system (specifically, with Object Storage).
 */
public interface DistributedManager {

  /**
   * Updates all objects in storage according to most recent cache and lag compensation technique
   */
  Set<LocalChange> synchronize();

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
  Serializable read(final GameObjectUUID gameObjectID, final String field, final int bufferIndex);

  /**
   * Sends request from authorObject to change the state of targetObject – state changes reflected in local non-canonical cache.
   *
   * @return if the write was accepted
   */
  boolean write(LocalChange change, GameObjectUUID author);

  /**
   * Sends request form author to change the state of target at the time indicated
   * Should only be used by game server when a client action is being applied to a non-client object
   *
   * @return if the write was accepted
   */
  boolean lagWrite(RemoteChange change, GameObjectUUID author);

  /**
   * Initializes a new GameObject in the store
   */
  GameObjectUUID create(HashMap<String, Serializable> fields, GameObjectUUID author, int bufferIndex);

  boolean delete(GameObjectUUID uuid, GameObjectUUID author, int bufferIndex);
}
