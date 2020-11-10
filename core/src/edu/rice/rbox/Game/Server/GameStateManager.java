package edu.rice.rbox.Game.Server;

import edu.rice.rbox.Common.Change.LocalFieldChange;
import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Game.Common.SyncState.GameStateDelta;
import edu.rice.rbox.ObjStorage.ObjectStore;

import java.util.Date;
import static edu.rice.rbox.Game.Common.SyncState.GameStateDelta.MovingRooms.NOT;
import static edu.rice.rbox.Game.Server.ObjectStorageKeys.Coin.HAS_BEEN_COLLECTED;
import static edu.rice.rbox.Game.Server.ObjectStorageKeys.Player.ROOM_ID;
import static edu.rice.rbox.Game.Server.ObjectStorageKeys.Room.PLAYERS_IN_ROOM;

public class GameStateManager {

  private ObjectStore objectStore;

  // This class will be responsible for taking in changes from the clients, resolving them, interacting with Object Storage, and returning a snapshot to send back to the game client.
  public void handleUpdateFromPlayer(GameStateDelta update) {
    final GameObjectUUID playerUUID = update.playerUUID;
//    final int bufferIndex = objectStore.getBufferIndex(update.timestamp);
    final int bufferIndex = 0;

    if (update.movingRooms != NOT) {
      // Remove from old room, add to new room, and give them a new position
//      final GameObjectUUID oldRoomUUID = roomForPlayer(player);
//
//      roomToPlayers.get(oldRoomUUID).remove(playerUUID);
      removePlayerFromRoom(playerUUID, roomForPlayer(playerUUID));

      final GameObjectUUID newRoomUUID = GameObjectUUID.randomUUID(); // TODO: calculate correct room UUID
      addPlayerToRoom(playerUUID, newRoomUUID);
    }
    // Update player position and score
    // Remove coins they have collected
    int coinsCollected = 0;
    for (GameObjectUUID coin : update.deletedCoins) {
      if (!((GameFieldBoolean) objectStore.read(coin, HAS_BEEN_COLLECTED, 0)).value) {
        coinsCollected++;
        objectStore.write(new LocalFieldChange(coin, HAS_BEEN_COLLECTED, new GameFieldBoolean(true), 0), coin);
       }
    }
//    update.updatedPlayerState.updateObjectStore(objectStore, bufferIndex);

    for (final GameObjectUUID collectedCoin : update.deletedCoins) {
      // TODO(Object Storage): do we need an "exists" method?
      // delete currently returns false only when a replica tries to delete something
      // There needs to be a way to check if the coin exists to be deleted.
      if (objectStore.delete(collectedCoin, playerUUID, bufferIndex)) {
//          final int score = objectStore.read(playerUUID, "score", bufferIndex);
//          objectStore.write(new LocalFieldChange(playerUUID, "score", score + 1, bufferIndex), playerUUID);
      }
    }
  }

  public void handlePlayerQuitting(GameObjectUUID player) {
    // Remove player from room
    // Deactivate/delete player
    removePlayerFromRoom(player, roomForPlayer(player));
  }

  public GameFieldSet<GameObjectUUID> playersInRoom(GameObjectUUID roomUUID) {
    return (GameFieldSet<GameObjectUUID>) objectStore.read(roomUUID, PLAYERS_IN_ROOM, 0);
  }

  public GameObjectUUID roomForPlayer(GameObjectUUID playerUUID) {
    return (GameObjectUUID) objectStore.read(playerUUID, ROOM_ID, 0);
  }

  public void addPlayerToRoom(GameObjectUUID playerUUID, GameObjectUUID roomUUID) {
    if (roomForPlayer(playerUUID).equals(roomUUID)) {
      throw new IllegalStateException("The player is already in this room, so we cannot add them.");
    }

    // Add player to room
    final GameFieldSet<GameObjectUUID> playersInRoom = playersInRoom(roomUUID);
    playersInRoom.add(playerUUID);
    objectStore.write(new LocalFieldChange(roomUUID, PLAYERS_IN_ROOM, playersInRoom, 0), roomUUID); // TODO: HashMap -> GameField
    // Set room in player
    objectStore.write(new LocalFieldChange(playerUUID, ROOM_ID, roomUUID, 0), playerUUID);
  }

  public void removePlayerFromRoom(GameObjectUUID playerUUID, GameObjectUUID roomUUID) {
    final GameFieldSet<GameObjectUUID> playersInRoom = playersInRoom(roomUUID);
    if (!objectStore.read(playerUUID, ROOM_ID, 0).equals(roomUUID) || !playersInRoom.contains(playerUUID)) {
      throw new IllegalStateException("The player is not in this room, so cannot remove them.");
    }
    // Update room's player list
    playersInRoom.remove(playerUUID);
    objectStore.write(new LocalFieldChange(playerUUID, PLAYERS_IN_ROOM, playersInRoom, 0), playerUUID); // TODO: HashMap -> GameField

    // NOTE: we will not update the player's room field, because this call is expected to be followed by adding the player to a new room,
    // or removing the player entirely.
  }

}