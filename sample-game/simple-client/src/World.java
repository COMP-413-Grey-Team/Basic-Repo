import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Set;

import sprites.LocalPlayerSprite;
import sprites.Sprite;
import utils.KeyState;

public class World extends JPanel {

  private final int WORLD_WIDTH = 1000;
  private final int WORLD_HEIGHT = 660;
  private final int DELTA_T = 17;

  private final LocalPlayerSprite player;
  private final Timer timer = new Timer(DELTA_T, (event) -> {
    updateState();
    repaint();
  });

  private Set<Sprite> otherPlayers;
  private final KeyState keyState = new KeyState();

  public World() { // TODO: add arguments so server can set initial state
    player = new LocalPlayerSprite(Color.BLUE, 50, 50, keyState);
    initWorld();
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
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    doDrawing(g);
  }

  private void doDrawing(Graphics g) {
    player.paint(g);
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
