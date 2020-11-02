package edu.rice.comp413fall2020grey.Game.Common.SyncState;

import edu.rice.comp413fall2020grey.Common.GameObjectUUID;

import java.util.HashSet;

// Sent from client to server
public class GameStateDelta {

  public GameObjectUUID playerUUID;
  public PlayerState updatedPlayerState;

  public HashSet<GameObjectUUID> deletedCoins;

  public GameStateDelta(GameObjectUUID playerUUID, PlayerState updatedPlayerState, HashSet<GameObjectUUID> deletedCoins) {
    this.playerUUID = playerUUID;
    this.updatedPlayerState = updatedPlayerState;
    this.deletedCoins = deletedCoins;
  }

}
