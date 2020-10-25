import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import sprites.CoinSprite;
import sprites.player.LocalPlayerSprite;
import sprites.Sprite;
import sprites.player.RemotePlayerSprite;
import utils.KeyState;

public class World extends JPanel {

  private final int WORLD_WIDTH = 1000;
  private final int WORLD_HEIGHT = 660;
  private final int DELTA_T = 17;

  private final UUID playerUUID = UUID.randomUUID();
  private final LocalPlayerSprite player;
  private final Timer timer = new Timer(DELTA_T, (event) -> {
    updateState();
    repaint();
  });

  private HashMap<UUID, RemotePlayerSprite> otherPlayers = new HashMap<>();
  private HashMap<UUID, CoinSprite> coins = new HashMap<>();

  private final KeyState keyState = new KeyState();

  public World() { // TODO: add arguments so server can set initial state
    player = new LocalPlayerSprite(Color.BLUE, 50, 50, 0, keyState);
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

  private void updateState() {
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

  private class GameKeyAdapter extends KeyAdapter {

    @Override
    public void keyPressed(KeyEvent e) {
      int key = e.getKeyCode();
      switch (key) {
        case KeyEvent.VK_A:
//          System.out.println("Clicked left!");
          keyState.tapped(KeyState.Key.A);
          break;
        case KeyEvent.VK_D:
//          System.out.println("Clicked right!");
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
