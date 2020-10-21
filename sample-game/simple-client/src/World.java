import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Set;
import sprites.PlayerSprite;
import sprites.Sprite;

public class World extends JPanel {

  private final int WORLD_WIDTH = 1000;
  private final int WORLD_HEIGHT = 660;
  private final double SPEED = 0.5;
  private final int DELTA_T = 17;

  private final PlayerSprite player;
  private final Timer timer = new Timer(DELTA_T, (event) -> {
    updateState();
    repaint();
  });

  private Set<Sprite> otherPlayers;

  public World() { // TODO: add arguments so server can set initial state
    player = new PlayerSprite(Color.BLUE, 50, 50);
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
        case KeyEvent.VK_LEFT:
//          System.out.println("Clicked left!");
          player.setVelX(-SPEED);
          break;
        case KeyEvent.VK_RIGHT:
//          System.out.println("Clicked right!");
          player.setVelX(SPEED);
          break;
        case KeyEvent.VK_UP:
          player.setVelY(-SPEED);
          break;
        case KeyEvent.VK_DOWN:
          player.setVelY(SPEED);
          break;
        default:
          break;
      }
      player.normalize(SPEED);
    }

    @Override
    public void keyReleased(KeyEvent e) {
      int key = e.getKeyCode();
      switch (key) {
        case KeyEvent.VK_LEFT:
        case KeyEvent.VK_RIGHT:
          player.setVelX(0);
          break;
        case KeyEvent.VK_UP:
        case KeyEvent.VK_DOWN:
          player.setVelY(0);
          break;
        default:
          break;
      }
      player.normalize(SPEED);
    }

  }

}
