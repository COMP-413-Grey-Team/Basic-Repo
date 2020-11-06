package edu.rice.rbox.ObjStorage;

import edu.rice.rbox.Common.Change.LocalChange;
import edu.rice.rbox.Common.GameObjectUUID;

<<<<<<< HEAD:core/src/edu/rice/rbox/ObjStorage/DistributedManager.java
import edu.rice.rbox.Common.Change.LocalFieldChange;
import java.io.Serializable;
=======
import edu.rice.comp413fall2020grey.Common.Change.LocalFieldChange;
import edu.rice.comp413fall2020grey.Common.GameField;
>>>>>>> ce7c210... getting all changes from master:core/src/edu/rice/comp413fall2020grey/ObjStorage/DistributedManager.java
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
  GameField read(final GameObjectUUID gameObjectID, final String field, final int bufferIndex);

  /**
   * Sends request from authorObject to change the state of targetObject – state changes reflected in local non-canonical cache.
   *
   * @return if the write was accepted
   */
  boolean write(LocalChange change, GameObjectUUID author);

  /**
   * Gets the buffer index for the given date.
   */
  int getBufferIndex(Date now);

  /**
   * Initializes a new GameObject in the store
   * interesting_fields is automatically populated with MODE, PREDICATE, and INTERESTING_FIELDS
   */
  GameObjectUUID create(HashMap<String, GameField> fields, HashSet<String> interesting_fields,
                        String predicate, GameObjectUUID author, int bufferIndex);

  /**
   * Deletes the specified object.
   *
   * @return Whether the delete was accepted.
   */
  boolean delete(GameObjectUUID uuid, GameObjectUUID author, int bufferIndex);
}
