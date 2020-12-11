package edu.rice.rbox.Game.Server;

import edu.rice.rbox.Common.Change.LocalAddReplicaChange;
import edu.rice.rbox.Common.Change.LocalChange;
import edu.rice.rbox.Common.Change.LocalDeleteReplicaChange;
import edu.rice.rbox.Common.Change.LocalFieldChange;
import edu.rice.rbox.Common.GameField.*;
import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Common.ServerUUID;
import edu.rice.rbox.Game.Client.Sprites.CoinSprite;
import edu.rice.rbox.Game.Common.SyncState.CoinState;
import edu.rice.rbox.Game.Common.SyncState.GameState;
import edu.rice.rbox.Game.Common.SyncState.GameStateDelta;
import edu.rice.rbox.Game.Common.SyncState.PlayerState;
import edu.rice.rbox.Location.interest.NoInterestPredicate;
import edu.rice.rbox.ObjStorage.ObjectStore;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import static edu.rice.rbox.Common.Constants.NUMBER_OF_ROOMS;
import static network.GameNetworkProto.UpdateFromClient.MovingRooms.NOT;
import static edu.rice.rbox.Game.Client.World.WORLD_HEIGHT;
import static edu.rice.rbox.Game.Client.World.WORLD_WIDTH;
import static edu.rice.rbox.Game.Server.ObjectStorageKeys.*;
import static network.GameNetworkProto.UpdateFromClient.MovingRooms.RIGHT;

public class GameStateManager {

  private final ServerUUID serverUUID;
  private Server2Store objectStore;

  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

  private Timer coinTimer = new Timer(500, e -> {
    final HashSet<GameObjectUUID> roomsToGen = new HashSet<>();

    final GameFieldSet<GameFieldInteger> roomIndices = (GameFieldSet<GameFieldInteger>) objectStore.read(Global.GLOBAL_OBJ, Global.keyForServerUUID(myServerUUID()), 0);
    lock.readLock().lock();
    for (final GameFieldInteger roomIndex : roomIndices) {
      final GameObjectUUID roomUUID =
          (GameObjectUUID) objectStore.read(Global.GLOBAL_OBJ, Global.roomKeyForIndex(roomIndex.getValue()), 0);
      final GameFieldSet<GameObjectUUID> coins =
          (GameFieldSet<GameObjectUUID>) objectStore.read(roomUUID, Room.COINS_IN_ROOM, 0);
      if (coins.size() < 25) {
        roomsToGen.add(roomUUID);
      }
    }
    lock.readLock().unlock();

    roomsToGen.forEach(this::createRandomCoin);
  });

  private Timer gameLoopTimer = new Timer(50, e -> {
    objectStore.advanceBuffer();
    objectStore.synchronize().forEach(localChange -> {
      objectStore.write(localChange.copyWithBufferIndex(0), localChange.getTarget());
      final GameFieldString type = (GameFieldString) objectStore.read(localChange.getTarget(), TYPE, 0);
      if (type.getValue().equals(PlayerScore.TYPE_NAME)) {
        GameFieldList<GameObjectUUID> leaderboard =
            (GameFieldList<GameObjectUUID>) objectStore.read(Leaderboard.GLOBAL_OBJ, Leaderboard.LEADERBOARD_VALUE, 0);
        if (localChange instanceof LocalAddReplicaChange) {
          leaderboard.add(localChange.getTarget());
        } else if (localChange instanceof LocalDeleteReplicaChange) {
          leaderboard.remove(localChange.getTarget());
        }
        leaderboard.sort(Comparator.comparing(scoreUUID -> ((GameFieldInteger) objectStore.read((GameObjectUUID) scoreUUID, PlayerScore.VALUE, 0)).getValue()).reversed());
        objectStore.write(new LocalFieldChange(Leaderboard.GLOBAL_OBJ, Leaderboard.LEADERBOARD_VALUE, leaderboard, 0), Leaderboard.GLOBAL_OBJ);
      }
    });
  });

  public GameStateManager(ServerUUID serverUUID, Server2Store objectStore) {
    this.serverUUID = serverUUID;
    this.objectStore = objectStore;
  }

  private ServerUUID myServerUUID() {
    return serverUUID;
  }

  private Random random = new Random();
  private int getRandomNumberUsingNextInt(int min, int max) {
    return random.nextInt(max - min) + min;
  }

  public GameState handlePlayerJoining(PlayerState newPlayerInfo) {
    // Find one of the rooms this server is in charge of and add the player to that room.
    final GameFieldSet<GameFieldInteger> roomIndices = (GameFieldSet<GameFieldInteger>) objectStore.read(Global.GLOBAL_OBJ, Global.keyForServerUUID(myServerUUID()), 0);
    final GameObjectUUID roomUUID =
        (GameObjectUUID) objectStore.read(Global.GLOBAL_OBJ, Global.roomKeyForIndex(roomIndices.iterator().next().getValue()), 0);

    HashMap<String, GameField> playerMap = new HashMap<>() {{
      put(TYPE, new GameFieldString(Player.TYPE_NAME));
      put(ObjectStorageKeys.Player.X_POS, new GameFieldDouble(getRandomNumberUsingNextInt(30, WORLD_WIDTH - 30)));
      put(ObjectStorageKeys.Player.Y_POS, new GameFieldDouble(getRandomNumberUsingNextInt(30, WORLD_HEIGHT - 30)));
      put(ObjectStorageKeys.Player.NAME, new GameFieldString(newPlayerInfo.name));
      put(ObjectStorageKeys.Player.COLOR, new GameFieldColor(newPlayerInfo.color));
      put(ObjectStorageKeys.Player.ROOM_ID, roomUUID);
    }};
    final GameObjectUUID newPlayerUUID = objectStore.create(playerMap, ObjectStorageKeys.Player.IMPORTANT_FIELDS, ObjectStorageKeys.Player.PREDICATE, roomUUID, 0);

    final GameObjectUUID scoreUUID = objectStore.create(new HashMap<>(){{
      put(TYPE, new GameFieldString(PlayerScore.TYPE_NAME));
      put(PlayerScore.VALUE, new GameFieldInteger(newPlayerInfo.score));
      put(PlayerScore.PLAYER_NAME, new GameFieldString(newPlayerInfo.name));
    }}, PlayerScore.IMPORTANT_FIELDS, new NoInterestPredicate(), newPlayerUUID, 0);
    objectStore.write(new LocalFieldChange(newPlayerUUID, Player.SCORE, scoreUUID, 0), newPlayerUUID);

    final GameFieldSet<GameObjectUUID> roomMembers =
        (GameFieldSet<GameObjectUUID>) objectStore.read(roomUUID, Room.PLAYERS_IN_ROOM, 0);
    roomMembers.add(newPlayerUUID);
    objectStore.write(new LocalFieldChange(roomUUID, Room.PLAYERS_IN_ROOM, roomMembers, 0), roomUUID);

    // TODO: add player to leaderboard list

    return gameStateForRoom(roomUUID, newPlayerUUID);
  }

  // This class will be responsible for taking in changes from the clients, resolving them, interacting with Object Storage, and returning a snapshot to send back to the game client.
  public GameState handleUpdateFromPlayer(GameStateDelta update) {
    final GameObjectUUID playerUUID = update.playerUUID;
    final int bufferIndex = 0;
    final GameObjectUUID roomUUID = roomForPlayer(playerUUID);

    if (update.movingRooms != NOT) {
      removePlayerFromRoom(playerUUID, roomUUID);

      int roomIndex = ((GameFieldInteger) objectStore.read(roomUUID, Room.ROOM_INDEX, 0)).getValue();

      int nextRoomIndex = (update.movingRooms == RIGHT) ? ((roomIndex + 1) % NUMBER_OF_ROOMS) : ((roomIndex - 1 + NUMBER_OF_ROOMS) % NUMBER_OF_ROOMS);
      GameObjectUUID nextRoomUUID = (GameObjectUUID) objectStore.read(Global.GLOBAL_OBJ, Global.roomKeyForIndex(nextRoomIndex), 0);

      final GameObjectUUID newRoomUUID = nextRoomUUID;
      addPlayerToRoom(playerUUID, newRoomUUID);

      return gameStateForRoom(newRoomUUID, playerUUID);
    }

    // Remove coins they have collected
    int coinsCollected = 0;
    if (!update.deletedCoins.isEmpty()) {
      lock.writeLock().lock();
      final GameFieldSet<GameObjectUUID> coinsInRoom = coinsInRoom(roomUUID);

      for (GameObjectUUID coin : update.deletedCoins) {
        if (!((GameFieldBoolean) objectStore.read(coin, ObjectStorageKeys.Coin.HAS_BEEN_COLLECTED, 0)).getValue()) {
          coinsCollected++;
          objectStore.write(new LocalFieldChange(coin,
              ObjectStorageKeys.Coin.HAS_BEEN_COLLECTED,
              new GameFieldBoolean(true),
              0), coin);
          coinsInRoom.remove(coin);
        }
      }
      objectStore.write(new LocalFieldChange(roomUUID, Room.COINS_IN_ROOM, coinsInRoom, 0), roomUUID);
      lock.writeLock().unlock();
    }

    // Updating player score
    final GameObjectUUID scoreUUID = (GameObjectUUID) objectStore.read(playerUUID, ObjectStorageKeys.Player.SCORE, 0);
    final int score = ((GameFieldInteger) objectStore.read(scoreUUID, PlayerScore.VALUE, 0)).getValue();
    final GameFieldInteger newScore = new GameFieldInteger(score + coinsCollected);
    objectStore.write(new LocalFieldChange(scoreUUID, PlayerScore.VALUE, newScore, 0), playerUUID);

    // Update position
    objectStore.write(new LocalFieldChange(playerUUID, ObjectStorageKeys.Player.X_POS, new GameFieldDouble(update.updatedPlayerState.x), 0), playerUUID);
    objectStore.write(new LocalFieldChange(playerUUID, ObjectStorageKeys.Player.Y_POS, new GameFieldDouble(update.updatedPlayerState.y), 0), playerUUID);

    return gameStateForRoom(roomUUID, playerUUID);
  }

  public void createRandomCoin(GameObjectUUID roomUUID) {
    final int x =
        ThreadLocalRandom.current().nextInt(CoinSprite.CIRCLE_RADIUS, WORLD_WIDTH - 2 * CoinSprite.CIRCLE_RADIUS);
    final int y =
        ThreadLocalRandom.current().nextInt(CoinSprite.CIRCLE_RADIUS, WORLD_HEIGHT - 2 * CoinSprite.CIRCLE_RADIUS);
    HashMap<String, GameField> coinValues = new HashMap<>() {{
      put(TYPE, new GameFieldString(Coin.TYPE_NAME));
      put(Coin.ROOM_ID, roomUUID);
      put(Coin.X_POS, new GameFieldInteger(x));
      put(Coin.Y_POS, new GameFieldInteger(y));
      put(Coin.HAS_BEEN_COLLECTED, new GameFieldBoolean(false));
    }};

    lock.writeLock().lock();
    final GameObjectUUID
        coinUUID =
        objectStore.create(coinValues, Coin.IMPORTANT_FIELDS, new NoInterestPredicate(), roomUUID, 0);

    final GameFieldSet<GameObjectUUID> coinsInRoom = coinsInRoom(roomUUID);
    coinsInRoom.add(coinUUID);
    objectStore.write(new LocalFieldChange(roomUUID, Room.COINS_IN_ROOM, coinsInRoom, 0), roomUUID);

    lock.writeLock().unlock();
  }

  public void handlePlayerQuitting(GameObjectUUID player) {
    // Remove player from room
    // Deactivate/delete player
    removePlayerFromRoom(player, roomForPlayer(player));

    final GameObjectUUID scoreUUID = (GameObjectUUID) objectStore.read(player, Player.SCORE, 0);
    objectStore.delete(scoreUUID, player);
    objectStore.delete(player, player);
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
    final GameObjectUUID scoreUUID = (GameObjectUUID) objectStore.read(player, Player.SCORE, 0);
    return new PlayerState(
        ((GameFieldInteger) objectStore.read(player, ObjectStorageKeys.Player.X_POS, 0)).getValue(),
        ((GameFieldInteger) objectStore.read(player, ObjectStorageKeys.Player.Y_POS, 0)).getValue(),
        ((GameFieldString) objectStore.read(player, ObjectStorageKeys.Player.NAME, 0)).getValue(),
        ((GameFieldColor) objectStore.read(player, ObjectStorageKeys.Player.COLOR, 0)).getValue(),
        ((GameFieldInteger) objectStore.read(player, PlayerScore.VALUE, 0)).getValue()
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

    lock.readLock().lock();
    final GameFieldSet<GameObjectUUID> coins = coinsInRoom(room);
    Map<GameObjectUUID, CoinState> coinsMap = coins.stream().collect(Collectors.toMap(
       coinUUID -> coinUUID,
       this::coinStateForCoin
    ));
    lock.readLock().unlock();

    GameFieldColor backgroundColor =
        (GameFieldColor) objectStore.read(room, ObjectStorageKeys.Room.BACKGROUND_COLOR, 0);
    return new GameState(player, playersMap, coinsMap, backgroundColor.getValue());
  }

  public void initializeRooms(Set<Integer> myRooms) {
    myRooms.forEach(roomIndex -> {
      final GameObjectUUID roomUUID = objectStore.create(new HashMap<>() {{
        put(TYPE, new GameFieldString(Room.TYPE_NAME));
        put(Room.PLAYERS_IN_ROOM, new GameFieldSet<>(new HashSet<>()));
        put(Room.COINS_IN_ROOM, new GameFieldSet<>(new HashSet<>()));
        put(Room.BACKGROUND_COLOR, new GameFieldColor(roomIndex % 2 == 0 ? Color.GRAY : Color.WHITE));
        put(Room.ROOM_INDEX, new GameFieldInteger(roomIndex));
      }}, Room.IMPORTANT_FIELDS, new NoInterestPredicate(), null, 0);
    });

    objectStore.write(
        new LocalFieldChange(
            Global.GLOBAL_OBJ,
            Global.keyForServerUUID(myServerUUID()),
            new GameFieldSet<GameFieldInteger>(myRooms.stream().map(GameFieldInteger::new).collect(Collectors.toSet())),
            0),
        null);

    coinTimer.start();
    gameLoopTimer.start();
  }

}