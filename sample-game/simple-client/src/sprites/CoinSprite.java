package sprites;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class CoinSprite extends Sprite {

  public static final int CIRCLE_RADIUS = 8;
  private static Color GOLD = Color.YELLOW;

  public CoinSprite(double x, double y) {
    super(x, y);
  }

  @Override
  public void paint(Graphics g) {
    g.setColor(GOLD);
    g.fillOval((int) this.getX() - CIRCLE_RADIUS, (int) this.getY() - CIRCLE_RADIUS, CIRCLE_RADIUS * 2, CIRCLE_RADIUS * 2);
  }
}
