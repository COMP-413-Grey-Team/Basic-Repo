import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Set;

public class World extends JPanel {

  private final int WORLD_WIDTH = 1000;
  private final int WORLD_HEIGHT = 660;
  private final double SPEED = 500;
  private final int DELTA_T = 17;
  private final int CIRCLE_RADIUS = 15;

  private final Sprite player;
  private double playerVelX = 0.0;
  private double playerVelY = 0.0;
  private final Timer timer = new Timer(DELTA_T, (event) -> {
    updateState();
    repaint();
  });

  private Set<Sprite> otherPlayers;

  public World() { // TODO: add arguments so server can set initial state
    player = new Sprite();
    player.x = 50;
    player.y = 50;
    player.color = Color.ORANGE;
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
    player.x += playerVelX * (DELTA_T / 1000.0);
    player.y += playerVelY * (DELTA_T / 1000.0);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    doDrawing(g);
  }

  private void doDrawing(Graphics g) {
    g.setColor(player.color);
    g.fillOval((int) player.x - CIRCLE_RADIUS, (int) player.y - CIRCLE_RADIUS, CIRCLE_RADIUS * 2, CIRCLE_RADIUS * 2);
  }

  private class GameKeyAdapter extends KeyAdapter {

    @Override
    public void keyPressed(KeyEvent e) {
      int key = e.getKeyCode();
      switch (key) {
        case KeyEvent.VK_LEFT:
          playerVelX = -SPEED;
          break;
        case KeyEvent.VK_RIGHT:
          playerVelX = SPEED;
          break;
        case KeyEvent.VK_UP:
          playerVelY = -SPEED;
          break;
        case KeyEvent.VK_DOWN:
          playerVelY = SPEED;
          break;
        default:
          break;
      }
    }

    @Override
    public void keyReleased(KeyEvent e) {
      int key = e.getKeyCode();
      switch (key) {
        case KeyEvent.VK_LEFT:
        case KeyEvent.VK_RIGHT:
          playerVelX = 0;
          break;
        case KeyEvent.VK_UP:
        case KeyEvent.VK_DOWN:
          playerVelY = 0;
          break;
        default:
          break;
      }
    }

  }

}
