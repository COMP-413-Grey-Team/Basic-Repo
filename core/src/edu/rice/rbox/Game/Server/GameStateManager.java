package edu.rice.rbox.Game.Server;

import edu.rice.rbox.Common.Change.LocalFieldChange;
import edu.rice.rbox.Common.GameField.*;
import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Game.Common.SyncState.CoinState;
import edu.rice.rbox.Game.Common.SyncState.GameState;
import edu.rice.rbox.Game.Common.SyncState.GameStateDelta;
import edu.rice.rbox.Game.Common.SyncState.PlayerState;
import edu.rice.rbox.ObjStorage.ObjectStore;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static network.GameNetworkProto.UpdateFromClient.MovingRooms.NOT;

public class GameStateManager {

  private ObjectStore objectStore;

  public GameState handlePlayerJoining(PlayerState newPlayerInfo) {
    GameFieldList<GameObjectUUID> roomOrder =
        (GameFieldList<GameObjectUUID>) objectStore.read(ObjectStorageKeys.Global.GLOBAL_OBJ, ObjectStorageKeys.Global.ROOM_ORDER, 0);
    GameObjectUUID roomUUID = roomOrder.get(0);

    HashMap<String, GameField> playerMap = new HashMap<>() {{
      put(ObjectStorageKeys.Player.X_POS, new GameFieldDouble(newPlayerInfo.x));
      put(ObjectStorageKeys.Player.Y_POS, new GameFieldDouble(newPlayerInfo.y));
      put(ObjectStorageKeys.Player.NAME, new GameFieldString(newPlayerInfo.name));
      put(ObjectStorageKeys.Player.COLOR, new GameFieldColor(newPlayerInfo.color));
      put(ObjectStorageKeys.Player.SCORE, new GameFieldInteger(newPlayerInfo.score));
      put(ObjectStorageKeys.Player.ROOM_ID, roomUUID); // TODO: which room? This is probably determined at startup time
    }};
    final GameObjectUUID newPlayerUUID = objectStore.create(playerMap, ObjectStorageKeys.Player.IMPORTANT_FIELDS, ObjectStorageKeys.Player.PREDICATE, roomUUID, 0);
    return gameStateForRoom(roomUUID, newPlayerUUID);
  }

  // This class will be responsible for taking in changes from the clients, resolving them, interacting with Object Storage, and returning a snapshot to send back to the game client.
  public GameState handleUpdateFromPlayer(GameStateDelta update) {
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
    if (coinsCollected != 0) {
      final GameFieldInteger score = (GameFieldInteger) objectStore.read(playerUUID, ObjectStorageKeys.Player.SCORE, 0);
      final GameFieldInteger newScore = new GameFieldInteger(score.getValue() + coinsCollected);
      objectStore.write(new LocalFieldChange(playerUUID, ObjectStorageKeys.Player.SCORE, newScore, 0), playerUUID);
    }

    // Update position
    objectStore.write(new LocalFieldChange(playerUUID, ObjectStorageKeys.Player.X_POS, new GameFieldDouble(update.updatedPlayerState.x), 0), playerUUID);
    objectStore.write(new LocalFieldChange(playerUUID, ObjectStorageKeys.Player.Y_POS, new GameFieldDouble(update.updatedPlayerState.y), 0), playerUUID);

    return gameStateForRoom(roomForPlayer(playerUUID), playerUUID);
  }

  public void handlePlayerQuitting(GameObjectUUID player) {
    // Remove player from room
    // Deactivate/delete player
    removePlayerFromRoom(player, roomForPlayer(player));
  }

  public GameFieldSet<GameObjectUUID> playersInRoom(GameObjectUUID roomUUID) {
    return (GameFieldSet<GameObjectUUID>) objectStore.read(roomUUID, ObjectStorageKeys.Room.PLAYERS_IN_ROOM, 0);
  }

  public GameFieldSet<GameObjectUUID> coinsInRoom(GameObjectUUID roomUUID) {
    return (GameFieldSet<GameObjectUUID>) objectStore.read(roomUUID, ObjectStorageKeys.Room.COINS_IN_ROOM, 0);
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

  public CoinState coinStateForCoin(GameObjectUUID coin) {
    return new CoinState(
        ((GameFieldInteger) objectStore.read(coin, ObjectStorageKeys.Player.X_POS, 0)).getValue(),
        ((GameFieldInteger) objectStore.read(coin, ObjectStorageKeys.Player.Y_POS, 0)).getValue()
    );
  }

  public GameState gameStateForRoom(GameObjectUUID room, GameObjectUUID player) {
    final GameFieldSet<GameObjectUUID> players = playersInRoom(room);
    Map<GameObjectUUID, PlayerState> playersMap = players.stream().collect(Collectors.toMap(
        playerUUID -> playerUUID,
        this::playerStateForPlayer
    ));

    final GameFieldSet<GameObjectUUID> coins = coinsInRoom(room);
    Map<GameObjectUUID, CoinState> coinsMap = coins.stream().collect(Collectors.toMap(
       coinUUID -> coinUUID,
       this::coinStateForCoin
    ));

    GameFieldColor backgroundColor =
        (GameFieldColor) objectStore.read(room, ObjectStorageKeys.Room.BACKGROUND_COLOR, 0);
    return new GameState(player, playersMap, coinsMap, backgroundColor.getValue());
  }

}