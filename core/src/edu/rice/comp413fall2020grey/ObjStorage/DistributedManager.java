package edu.rice.comp413fall2020grey.ObjStorage;

/**
 * This will the the interface exposed to the Game Server to interact with the distributed system (specifically, with Object Storage).
 */
public interface DistributedManager {

  /**
   * Called each update cycle by the game server to update its game objects.
   */
  // TODO: this will need a non-void return type containing the changes for the game server.
  void synchronize();

}
