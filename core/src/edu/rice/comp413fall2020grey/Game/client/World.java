package edu.rice.comp413fall2020grey.Game.Client;

import edu.rice.comp413fall2020grey.Game.Common.Sprites.CoinSprite;
import edu.rice.comp413fall2020grey.Game.Common.Sprites.Players.LocalPlayerSprite;
import edu.rice.comp413fall2020grey.Game.Common.Sprites.Players.RemotePlayerSprite;
import edu.rice.comp413fall2020grey.Game.Common.SyncState.CoinState;
import edu.rice.comp413fall2020grey.Game.Common.SyncState.GameStateDelta;
import edu.rice.comp413fall2020grey.Game.Common.SyncState.PlayerState;
import edu.rice.comp413fall2020grey.Game.Common.Utils.KeyState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class World extends JPanel {

  private final int WORLD_WIDTH = 1000;
  private final int WORLD_HEIGHT = 660;
  private final int DELTA_T = 17;

  private final Timer timer = new Timer(DELTA_T, (event) -> {
    mergeRemoteChanges();
    updateInternalState();
    repaint();
    sendUpdatesToServerAsynchronously();
  });

  // Local State
  private final UUID playerUUID = UUID.randomUUID();
  private LocalPlayerSprite player;
  private Map<UUID, RemotePlayerSprite> otherPlayers = new HashMap<>();
  private Map<UUID, CoinSprite> coins = new HashMap<>();

  // Local changes that need to be synced to the server
  private HashSet<UUID> deletedCoins = new HashSet<>();

  // Remote changes that have yet to be merged locally
  private HashMap<UUID, PlayerState> updatedPlayers = new HashMap<>();
  private HashMap<UUID, CoinState> newCoins = new HashMap<>();

  private final KeyState keyState = new KeyState();

  public World(PlayerState playerState) { // TODO: add arguments so server can set initial state
    this.player = new LocalPlayerSprite(playerState.color, playerState.x, playerState.y, playerState.score, playerState.name, keyState);
    for (int i = 0; i < 20; i++) {
      coins.put(UUID.randomUUID(), randomCoin());
    }
    initWorld();
  }

  private CoinSprite randomCoin() {
    return new CoinSprite(ThreadLocalRandom.current().nextInt(CoinSprite.CIRCLE_RADIUS, WORLD_WIDTH - 2 * CoinSprite.CIRCLE_RADIUS),
        ThreadLocalRandom.current().nextInt(CoinSprite.CIRCLE_RADIUS, WORLD_HEIGHT - 2 * CoinSprite.CIRCLE_RADIUS));
  }

  private void initWorld() {
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
    player.paint(g);
    otherPlayers.values().forEach(p -> p.paint(g));
    coins.values().forEach(c -> c.paint(g));

    g.setColor(Color.black);
    g.drawString("Score: " + player.getScore(), 10, 20);
  }

  private void mergeRemoteChanges() {
    final PlayerState playerState = updatedPlayers.get(playerUUID);

    if (playerState != null) {
      player =
          new LocalPlayerSprite(playerState.color,
              playerState.x,
              playerState.y,
              playerState.score,
              playerState.name,
              keyState);
    }

    otherPlayers = updatedPlayers.entrySet().stream().filter(entry -> !entry.getKey().equals(playerUUID)).collect(Collectors.toMap(
        Map.Entry::getKey,
       entry -> new RemotePlayerSprite(entry.getValue().color, entry.getValue().x, entry.getValue().y, 0, 0, entry.getValue().score, entry.getValue().name)
    ));

    coins = newCoins.entrySet().stream().collect(Collectors.toMap(
        Map.Entry::getKey,
       entry -> new CoinSprite(entry.getValue().x, entry.getValue().y)
    ));
  }

  private void sendUpdatesToServerAsynchronously() {
    // TODO: send this to the server
    new GameStateDelta(playerUUID, player.getPlayerState(), deletedCoins);

    deletedCoins = new HashSet<>();
  }

  private void handleServerUpdatesAsynchronously(HashMap<UUID, PlayerState> playerStates, HashMap<UUID, CoinState> coinStates) {
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
