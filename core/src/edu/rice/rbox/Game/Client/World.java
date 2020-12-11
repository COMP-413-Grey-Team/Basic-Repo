package edu.rice.rbox.Game.Client;

import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Game.Client.Sprites.CoinSprite;
import edu.rice.rbox.Game.Client.Sprites.DoorSprite;
import edu.rice.rbox.Game.Client.Sprites.Players.LocalPlayerSprite;
import edu.rice.rbox.Game.Client.Sprites.Players.RemotePlayerSprite;
import edu.rice.rbox.Game.Common.SyncState.CoinState;
import edu.rice.rbox.Game.Common.SyncState.GameState;
import edu.rice.rbox.Game.Common.SyncState.GameStateDelta;
import edu.rice.rbox.Game.Common.SyncState.PlayerState;
import edu.rice.rbox.Game.Common.Utils.KeyState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static network.GameNetworkProto.UpdateFromClient.MovingRooms.NOT;

public class World extends JPanel {

  public static final int WORLD_WIDTH = 1000;
  public static final int WORLD_HEIGHT = 660;
  private final int DELTA_T = 17;

  private final Timer timer = new Timer(DELTA_T, (event) -> {
    mergeRemoteChanges();
    updateInternalState();
    repaint();
    Thread t = new Thread(() -> {
      sendUpdatesToServerAsynchronously();
    });
    t.start();
  });

  // Local State
  private final GameObjectUUID playerUUID = GameObjectUUID.randomUUID();
  public LocalPlayerSprite player;
  private Map<GameObjectUUID, RemotePlayerSprite> otherPlayers = new HashMap<>();
  private Map<GameObjectUUID, CoinSprite> coins = new HashMap<>();
//  private final DoorSprite leftDoor = new DoorSprite(DoorSprite.DoorSide.LEFT, WORLD_WIDTH, WORLD_HEIGHT);
//  private final DoorSprite rightDoor = new DoorSprite(DoorSprite.DoorSide.RIGHT, WORLD_WIDTH, WORLD_HEIGHT);

//  private DoorSprite.DoorSide shouldMoveRooms = null;

  // Local changes that need to be synced to the server
  private HashSet<GameObjectUUID> deletedCoins = new HashSet<>();

  // Remote changes that have yet to be merged locally
  private HashMap<GameObjectUUID, PlayerState> updatedPlayers = new HashMap<>();
  private HashMap<GameObjectUUID, CoinState> newCoins = new HashMap<>();

  private Color backgroundColor = Color.lightGray;

  private final KeyState keyState = new KeyState();
  private GameClientGrpc clientGrpc;

  public World(PlayerState playerState, GameClientGrpc clientGrpc) { // TODO: add arguments so server can set initial state
    this.player = new LocalPlayerSprite(playerState.x, playerState.y, playerState.score, playerState.name, keyState);
    for (int i = 0; i < 20; i++) {
      coins.put(GameObjectUUID.randomUUID(), randomCoin());
    }
    this.clientGrpc = clientGrpc;
  }

  private CoinSprite randomCoin() {
    return new CoinSprite(ThreadLocalRandom.current().nextInt(CoinSprite.CIRCLE_RADIUS, WORLD_WIDTH - 2 * CoinSprite.CIRCLE_RADIUS),
        ThreadLocalRandom.current().nextInt(CoinSprite.CIRCLE_RADIUS, WORLD_HEIGHT - 2 * CoinSprite.CIRCLE_RADIUS));
  }

  public void initWorld() {
    addKeyListener(new GameKeyAdapter());
    setBackground(Color.CYAN);
    setFocusable(true);
    setPreferredSize(new Dimension(WORLD_WIDTH, WORLD_HEIGHT));

    timer.start();
  }

  private void updateInternalState() {
    player.updateState(DELTA_T, WORLD_WIDTH, WORLD_HEIGHT);

    player.checkCoinCollisions(coins).forEach(coins::remove);

    otherPlayers.forEach((uuid, sprite) -> {
      sprite.updateState(DELTA_T, WORLD_WIDTH, WORLD_HEIGHT);
      sprite.checkCoinCollisions(coins).forEach(coins::remove);
    });
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    doDrawing(g);
  }

  private void doDrawing(Graphics g) {
    g.setColor(backgroundColor);
    g.fillRect(0, 0, getWidth(), getHeight());

//    leftDoor.paint(g);
//    rightDoor.paint(g);
    coins.values().forEach(c -> c.paint(g));
    otherPlayers.values().forEach(p -> p.paint(g));
    player.paint(g);

//    // TODO: Maybe I can make the client send a leave
//    //  message when it touches a door?
//    if (leftDoor.containsPoint(player.getX(), player.getY())
//            || rightDoor.containsPoint(player.getX(), player.getY())){
//      System.out.println("Should remove the player from object storage");
//      this.clientGrpc.remove(playerUUID);
//    }

    g.setColor(Color.black);
    g.drawString("Score: " + player.getScore(), 10, 20);
  }

  private void mergeRemoteChanges() {
    final PlayerState playerState = updatedPlayers.get(playerUUID);

    if (playerState != null) {
      player =
          new LocalPlayerSprite(
              playerState.x,
              playerState.y,
              playerState.score,
              playerState.name,
              keyState);
    }

    otherPlayers = updatedPlayers.entrySet().stream().filter(entry -> !entry.getKey().equals(playerUUID)).collect(Collectors.toMap(
        Map.Entry::getKey,
       entry -> new RemotePlayerSprite(entry.getValue().x, entry.getValue().y, 0, 0, entry.getValue().score, entry.getValue().name)
    ));

    coins = newCoins.entrySet().stream().collect(Collectors.toMap(
        Map.Entry::getKey,
       entry -> new CoinSprite(entry.getValue().x, entry.getValue().y)
    ));
  }

//  private void trySwitchingRooms() {
//    // Check collision with door
//    DoorSprite[] doors = { leftDoor, rightDoor };
//    for (DoorSprite door : doors) {
//      if (door.containsPoint(player.getX(), player.getY())) {
//        shouldMoveRooms = door.doorSide;
//        System.out.println("DOOR " + door.doorSide);
//        break;
//      }
//    }
//  }

  private void sendUpdatesToServerAsynchronously() {
    GameState response = this.clientGrpc.update(new GameStateDelta(playerUUID, player.getPlayerState(), deletedCoins, NOT));
    this.handleServerUpdatesAsynchronously(response.playerStates, response.coinStates);
    deletedCoins = new HashSet<>();

    // TODO: include the direction we're switching to
  }

  public void handleServerUpdatesAsynchronously(Map<GameObjectUUID, PlayerState> playerStates, Map<GameObjectUUID, CoinState> coinStates) {
    playerStates.forEach(updatedPlayers::put);
    coinStates.forEach(newCoins::put);
  }

  private class GameKeyAdapter extends KeyAdapter {

    @Override
    public void keyPressed(KeyEvent e) {
      int key = e.getKeyCode();
      switch (key) {
        case KeyEvent.VK_A:
          keyState.tapped(KeyState.Key.A);
          break;
        case KeyEvent.VK_D:
          keyState.tapped(KeyState.Key.D);
          break;
        case KeyEvent.VK_W:
          keyState.tapped(KeyState.Key.W);
          break;
        case KeyEvent.VK_S:
          keyState.tapped(KeyState.Key.S);
          break;
//        case KeyEvent.VK_SPACE:
//          trySwitchingRooms();
//          break;
        default:
          break;
      }
      player.updateVelocity();
    }

    @Override
    public void keyReleased(KeyEvent e) {
      int key = e.getKeyCode();
      switch (key) {
        case KeyEvent.VK_A:
          keyState.released(KeyState.Key.A);
          break;
        case KeyEvent.VK_D:
          keyState.released(KeyState.Key.D);
          break;
        case KeyEvent.VK_W:
          keyState.released(KeyState.Key.W);
          break;
        case KeyEvent.VK_S:
          keyState.released(KeyState.Key.S);
          break;
        default:
          break;
      }
      player.updateVelocity();
    }

  }

}