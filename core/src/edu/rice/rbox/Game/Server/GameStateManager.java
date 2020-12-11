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

import static edu.rice.rbox.Game.Client.World.WORLD_HEIGHT;
import static edu.rice.rbox.Game.Client.World.WORLD_WIDTH;

public class GameStateManager {

  private GameObjectUUID roomUUID;

  private Server2Store objectStore;

  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

  int _timer = 0;

  private Timer gameLoopTimer = new Timer(50, e -> {
    objectStore.advanceBuffer();
    objectStore.synchronize().forEach(localChange -> {
      objectStore.write(localChange.copyWithBufferIndex(0), localChange.getTarget());
    });

    _timer += 50;
    if (_timer >= 500) {
      _timer = 0;
    }

    lock.readLock().lock();
    final GameFieldSet<GameObjectUUID> coins = (GameFieldSet<GameObjectUUID>) objectStore.read(roomUUID, ObjectStorageKeys.Room.COINS_IN_ROOM, 0);

    lock.readLock().unlock();

    if (coins.size() < 25) {
      createRandomCoin(roomUUID);
    }
  });

  public GameStateManager(Server2Store objectStore) {
    this.objectStore = objectStore;
  }

  private Random random = new Random();
  private int getRandomNumberUsingNextInt(int min, int max) {
    return random.nextInt(max - min) + min;
  }

  public GameState handlePlayerJoining(PlayerState newPlayerInfo) {
    HashMap<String, GameField> playerMap = new HashMap<>() {{
      put(ObjectStorageKeys.TYPE, new GameFieldString(ObjectStorageKeys.Player.TYPE_NAME));
      put(ObjectStorageKeys.Player.X_POS, new GameFieldDouble(getRandomNumberUsingNextInt(30, WORLD_WIDTH - 30)));
      put(ObjectStorageKeys.Player.Y_POS, new GameFieldDouble(getRandomNumberUsingNextInt(30, WORLD_HEIGHT - 30)));
      put(ObjectStorageKeys.Player.NAME, new GameFieldString(newPlayerInfo.name));
      put(ObjectStorageKeys.Player.COIN_COUNT, new GameFieldInteger(0));
      put(ObjectStorageKeys.Player.ROOM_ID, roomUUID);
    }};
    final GameObjectUUID newPlayerUUID = objectStore.create(playerMap, ObjectStorageKeys.Player.IMPORTANT_FIELDS, ObjectStorageKeys.Player.PREDICATE, roomUUID, 0);

    final GameFieldSet<GameObjectUUID> roomMembers =
        (GameFieldSet<GameObjectUUID>) objectStore.read(roomUUID, ObjectStorageKeys.Room.PLAYERS_IN_ROOM, 0);
    roomMembers.add(newPlayerUUID);
    objectStore.write(new LocalFieldChange(roomUUID, ObjectStorageKeys.Room.PLAYERS_IN_ROOM, roomMembers, 0), roomUUID);

    return gameStateForRoom(roomUUID, newPlayerUUID);
  }

  // This class will be responsible for taking in changes from the clients, resolving them, interacting with Object Storage, and returning a snapshot to send back to the game client.
  public GameState handleUpdateFromPlayer(GameStateDelta update) {
    final GameObjectUUID playerUUID = update.playerUUID;
    final int bufferIndex = 0;
//    final GameObjectUUID roomUUID = roomForPlayer(playerUUID);

    // Remove coins they have collected
    int coinsCollected = 0;
    if (!update.deletedCoins.isEmpty()) {
      lock.writeLock().lock();
      System.out.println(update.deletedCoins.size());
      final HashSet<GameObjectUUID> coinsInRoom = new HashSet<>(coinsInRoom(roomUUID).set);

      for (GameObjectUUID coin : update.deletedCoins) {
          coinsCollected++;
          objectStore.delete(coin, coin);
          coinsInRoom.removeIf(c -> c.getUUID().equals(coin.getUUID()));
      }
      objectStore.write(new LocalFieldChange(roomUUID, ObjectStorageKeys.Room.COINS_IN_ROOM, new GameFieldSet<GameObjectUUID>(coinsInRoom), 0), roomUUID);
      lock.writeLock().unlock();
    }

    // Updating player score
    final GameFieldInteger coinCount = (GameFieldInteger) objectStore.read(playerUUID, ObjectStorageKeys.Player.COIN_COUNT, 0);
    objectStore.write(new LocalFieldChange(playerUUID, ObjectStorageKeys.Player.COIN_COUNT, new GameFieldInteger(coinCount.getValue() + coinsCollected), 0), playerUUID);

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
      put(ObjectStorageKeys.TYPE, new GameFieldString(ObjectStorageKeys.Coin.TYPE_NAME));
      put(ObjectStorageKeys.Coin.ROOM_ID, roomUUID);
      put(ObjectStorageKeys.Coin.X_POS, new GameFieldDouble(x));
      put(ObjectStorageKeys.Coin.Y_POS, new GameFieldDouble(y));
    }};

    lock.writeLock().lock();
    final GameObjectUUID
        coinUUID =
        objectStore.create(coinValues, ObjectStorageKeys.Coin.IMPORTANT_FIELDS, new NoInterestPredicate(), roomUUID, 0);

    final GameFieldSet<GameObjectUUID> coinsInRoom = coinsInRoom(roomUUID);
    coinsInRoom.add(coinUUID);
    objectStore.write(new LocalFieldChange(roomUUID, ObjectStorageKeys.Room.COINS_IN_ROOM, coinsInRoom, 0), roomUUID);

    lock.writeLock().unlock();
  }

  public void handlePlayerQuitting(GameObjectUUID player) {
    // Remove player from room
    // Deactivate/delete player
    removePlayerFromRoom(player, roomForPlayer(player));

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

  public void removePlayerFromRoom(GameObjectUUID playerUUID, GameObjectUUID roomUUID) {
    final GameFieldSet<GameObjectUUID> playersInRoom = playersInRoom(roomUUID);
    if (!objectStore.read(playerUUID, ObjectStorageKeys.Player.ROOM_ID, 0).equals(roomUUID) || !playersInRoom.contains(playerUUID)) {
      throw new IllegalStateException("The player is not in this room, so cannot remove them.");
    }
    // Update room's player list
    playersInRoom.remove(playerUUID);
    objectStore.write(new LocalFieldChange(playerUUID, ObjectStorageKeys.Room.PLAYERS_IN_ROOM, playersInRoom, 0), playerUUID);

    // NOTE: we will not update the player's room field, because this call is expected to be followed by adding the player to a new room,
    // or removing the player entirely.
  }

  public PlayerState playerStateForPlayer(GameObjectUUID player) {
    return new PlayerState(
        ((GameFieldDouble) objectStore.read(player, ObjectStorageKeys.Player.X_POS, 0)).getValue(),
        ((GameFieldDouble) objectStore.read(player, ObjectStorageKeys.Player.Y_POS, 0)).getValue(),
        ((GameFieldString) objectStore.read(player, ObjectStorageKeys.Player.NAME, 0)).getValue(),
        ((GameFieldInteger) objectStore.read(player, ObjectStorageKeys.Player.COIN_COUNT, 0)).getValue()
    );
  }

  public CoinState coinStateForCoin(GameObjectUUID coin) {
    return new CoinState(
        ((GameFieldDouble) objectStore.read(coin, ObjectStorageKeys.Player.X_POS, 0)).getValue(),
        ((GameFieldDouble) objectStore.read(coin, ObjectStorageKeys.Player.Y_POS, 0)).getValue()
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

  public void initializeRoom() {
    this.roomUUID = objectStore.create(new HashMap<>() {{
      put(ObjectStorageKeys.TYPE, new GameFieldString(ObjectStorageKeys.Room.TYPE_NAME));
      put(ObjectStorageKeys.Room.PLAYERS_IN_ROOM, new GameFieldSet<>(new HashSet<>()));
      put(ObjectStorageKeys.Room.COINS_IN_ROOM, new GameFieldSet<>(new HashSet<>()));
      put(ObjectStorageKeys.Room.BACKGROUND_COLOR, new GameFieldColor(Color.CYAN));
    }}, ObjectStorageKeys.Room.IMPORTANT_FIELDS, new NoInterestPredicate(), null, 0);

    gameLoopTimer.start();
  }

}