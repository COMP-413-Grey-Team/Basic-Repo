package edu.rice.rbox.Game.Server;

import edu.rice.rbox.Common.Change.LocalFieldChange;
import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Game.Client.Game;
import edu.rice.rbox.Game.Common.SyncState.GameState;
import edu.rice.rbox.Game.Common.SyncState.GameStateDelta;
import edu.rice.rbox.ObjStorage.ObjectStore;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import static edu.rice.rbox.Game.Common.SyncState.GameStateDelta.MovingRooms.NOT;

public class GameStateManager {

  private ObjectStore objectStore;

  private HashMap<GameObjectUUID, HashSet<GameObjectUUID>> roomToPlayers;
  private HashMap<GameObjectUUID, GameObjectUUID> playerToRoom;

  private HashMap<GameObjectUUID, HashSet<GameObjectUUID>> roomToCoins;
  private HashMap<GameObjectUUID, GameObjectUUID> roomToCoinSpawner;

  // This class will be responsible for taking in changes from the clients, resolving them, interacting with Object Storage, and returning a snapshot to send back to the game client.
  public void handleUpdateFromPlayer(GameStateDelta update) {
    final GameObjectUUID playerUUID = update.playerUUID;
//    final int bufferIndex = objectStore.getBufferIndex(update.timestamp);
    final int bufferIndex = 0;

    if (update.movingRooms != NOT) {
      // Remove from old room, add to new room, and give them a new position
      final GameObjectUUID oldRoomUUID = playerToRoom.get(playerUUID);
      roomToPlayers.get(oldRoomUUID).remove(playerUUID);
      final GameObjectUUID newRoomUUID = GameObjectUUID.randomUUID(); // TODO: calculate correct room UUID
      roomToPlayers.get(newRoomUUID).add(playerUUID); // TODO: make sure a HashSet exists for this key

      playerToRoom.put(playerUUID, newRoomUUID);
    } else {
      // Update player position and score
      // Remove coins they have collected
//      update.updatedPlayerState.updateObjectStore(objectStore, bufferIndex);

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
  }

  public void handlePlayerQuitting(GameObjectUUID player, Date timestamp) {
    final GameObjectUUID roomUUID = playerToRoom.get(player);
    playerToRoom.remove(player);
    roomToPlayers.get(roomUUID).remove(player);
    objectStore.delete(player, player, objectStore.getBufferIndex(timestamp));
  }

}
