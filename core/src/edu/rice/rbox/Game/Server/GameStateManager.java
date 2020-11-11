package edu.rice.rbox.Game.Server;

import edu.rice.rbox.Common.Change.LocalFieldChange;
import edu.rice.rbox.Common.GameField.*;
import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Game.Common.SyncState.GameStateDelta;
import edu.rice.rbox.Game.Common.SyncState.PlayerState;
import edu.rice.rbox.ObjStorage.ObjectStore;

import static edu.rice.rbox.Game.Common.SyncState.GameStateDelta.MovingRooms.NOT;

public class GameStateManager {

  private ObjectStore objectStore;

  // This class will be responsible for taking in changes from the clients, resolving them, interacting with Object Storage, and returning a snapshot to send back to the game client.
  public void handleUpdateFromPlayer(GameStateDelta update) {
    final GameObjectUUID playerUUID = update.playerUUID;
//    final int bufferIndex = objectStore.getBufferIndex(update.timestamp);
    final int bufferIndex = 0;

    if (update.movingRooms != NOT) {
      removePlayerFromRoom(playerUUID, roomForPlayer(playerUUID));

      final GameObjectUUID newRoomUUID = GameObjectUUID.randomUUID(); // TODO: calculate correct room UUID
      addPlayerToRoom(playerUUID, newRoomUUID);
    }

    // Update player score
    // Remove coins they have collected
    int coinsCollected = 0;
    for (GameObjectUUID coin : update.deletedCoins) {
      if (!((GameFieldBoolean) objectStore.read(coin, ObjectStorageKeys.Coin.HAS_BEEN_COLLECTED, 0)).getValue()) {
        coinsCollected++;
        objectStore.write(new LocalFieldChange(coin, ObjectStorageKeys.Coin.HAS_BEEN_COLLECTED, new GameFieldBoolean(true), 0), coin);
       }
    }
    final GameFieldInteger score = (GameFieldInteger) objectStore.read(playerUUID, ObjectStorageKeys.Player.SCORE, 0);
    final GameFieldInteger newScore = new GameFieldInteger(score.getValue() + coinsCollected);
    objectStore.write(new LocalFieldChange(playerUUID, ObjectStorageKeys.Player.SCORE, newScore, 0), playerUUID);
    // Update position
    objectStore.write(new LocalFieldChange(playerUUID, ObjectStorageKeys.Player.X_POS, new GameFieldDouble(update.updatedPlayerState.x), 0), playerUUID);
    objectStore.write(new LocalFieldChange(playerUUID, ObjectStorageKeys.Player.Y_POS, new GameFieldDouble(update.updatedPlayerState.y), 0), playerUUID);
  }

  public void handlePlayerQuitting(GameObjectUUID player) {
    // Remove player from room
    // Deactivate/delete player
    removePlayerFromRoom(player, roomForPlayer(player));
  }

  public GameFieldSet<GameObjectUUID> playersInRoom(GameObjectUUID roomUUID) {
    return (GameFieldSet<GameObjectUUID>) objectStore.read(roomUUID, ObjectStorageKeys.Room.PLAYERS_IN_ROOM, 0);
  }

  public GameObjectUUID roomForPlayer(GameObjectUUID playerUUID) {
    return (GameObjectUUID) objectStore.read(playerUUID, ObjectStorageKeys.Player.ROOM_ID, 0);
  }

  public void addPlayerToRoom(GameObjectUUID playerUUID, GameObjectUUID roomUUID) {
    if (roomForPlayer(playerUUID).equals(roomUUID)) {
      throw new IllegalStateException("The player is already in this room, so we cannot add them.");
    }

    // Add player to room
    final GameFieldSet<GameObjectUUID> playersInRoom = playersInRoom(roomUUID);
    playersInRoom.add(playerUUID);
    objectStore.write(new LocalFieldChange(roomUUID, ObjectStorageKeys.Room.PLAYERS_IN_ROOM, playersInRoom, 0), roomUUID); // TODO: HashMap -> GameField
    // Set room in player
    objectStore.write(new LocalFieldChange(playerUUID, ObjectStorageKeys.Player.ROOM_ID, roomUUID, 0), playerUUID);
  }

  public void removePlayerFromRoom(GameObjectUUID playerUUID, GameObjectUUID roomUUID) {
    final GameFieldSet<GameObjectUUID> playersInRoom = playersInRoom(roomUUID);
    if (!objectStore.read(playerUUID, ObjectStorageKeys.Player.ROOM_ID, 0).equals(roomUUID) || !playersInRoom.contains(playerUUID)) {
      throw new IllegalStateException("The player is not in this room, so cannot remove them.");
    }
    // Update room's player list
    playersInRoom.remove(playerUUID);
    objectStore.write(new LocalFieldChange(playerUUID, ObjectStorageKeys.Room.PLAYERS_IN_ROOM, playersInRoom, 0), playerUUID); // TODO: HashMap -> GameField

    // NOTE: we will not update the player's room field, because this call is expected to be followed by adding the player to a new room,
    // or removing the player entirely.
  }

  public PlayerState playerStateForPlayer(GameObjectUUID player) {
    return new PlayerState(
        ((GameFieldInteger) objectStore.read(player, ObjectStorageKeys.Player.X_POS, 0)).getValue(),
        ((GameFieldInteger) objectStore.read(player, ObjectStorageKeys.Player.Y_POS, 0)).getValue(),
        ((GameFieldString) objectStore.read(player, ObjectStorageKeys.Player.NAME, 0)).getValue(),
        ((GameFieldColor) objectStore.read(player, ObjectStorageKeys.Player.COLOR, 0)).getValue(),
        ((GameFieldInteger) objectStore.read(player, ObjectStorageKeys.Player.SCORE, 0)).getValue()
    );
  }
}