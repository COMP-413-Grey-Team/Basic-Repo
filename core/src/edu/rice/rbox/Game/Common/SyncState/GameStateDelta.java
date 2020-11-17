package edu.rice.rbox.Game.Common.SyncState;

import edu.rice.rbox.Common.GameObjectUUID;
import network.GameNetworkProto;

import java.util.HashSet;

// Sent from client to server
public class GameStateDelta {

  public GameObjectUUID playerUUID;
  public PlayerState updatedPlayerState;
  public GameNetworkProto.UpdateFromClient.MovingRooms movingRooms;

  public HashSet<GameObjectUUID> deletedCoins;

  public GameStateDelta(GameObjectUUID playerUUID, PlayerState updatedPlayerState, HashSet<GameObjectUUID> deletedCoins, GameNetworkProto.UpdateFromClient.MovingRooms movingRooms) {
    this.playerUUID = playerUUID;
    this.updatedPlayerState = updatedPlayerState;
    this.deletedCoins = deletedCoins;
    this.movingRooms = movingRooms;
  }

}
