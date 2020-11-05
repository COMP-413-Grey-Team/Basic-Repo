package edu.rice.rbox.Game.Common.SyncState;

import edu.rice.rbox.Common.GameObjectUUID;

import java.util.Date;
import java.util.HashSet;

// Sent from client to server
public class GameStateDelta {

  public enum MovingRooms {
    NOT,
    LEFT,
    RIGHT;
  }

  public Date timestamp;
  public GameObjectUUID playerUUID;
  public PlayerState updatedPlayerState;
  public MovingRooms movingRooms;

  public HashSet<GameObjectUUID> deletedCoins;

  public GameStateDelta(GameObjectUUID playerUUID, PlayerState updatedPlayerState, HashSet<GameObjectUUID> deletedCoins, MovingRooms movingRooms) {
    this.playerUUID = playerUUID;
    this.updatedPlayerState = updatedPlayerState;
    this.deletedCoins = deletedCoins;
    this.movingRooms = movingRooms;
  }

}
