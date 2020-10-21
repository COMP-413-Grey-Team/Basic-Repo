package sprites;

import java.awt.*;

public abstract class PlayerSprite extends Sprite {

  public static final int CIRCLE_RADIUS = 15;
  public static final double SPEED = 0.5;

  private double velX;
  private double velY;

  private Color color;

  public PlayerSprite(Color color, double x, double y, double velX, double velY) {
    super(x, y);
    this.color = color;
    this.velX = velX;
    this.velY = velY;
  }

  @Override
  public void paint(Graphics g) {
    g.setColor(this.getColor());
    g.fillOval((int) this.getX() - CIRCLE_RADIUS, (int) this.getY() - CIRCLE_RADIUS,
        CIRCLE_RADIUS * 2, CIRCLE_RADIUS * 2);
  }

  public void updateState(int time, int boardWidth, int boardHeight) {
    this.move(time, boardWidth, boardHeight);
  }

  public abstract void move(int time, double boardWidth, double boardHeight);

  public static double clamp(double min, double max, double radius, double expr) {
    return Math.min(max - radius, Math.max(min + radius, expr));
  }

  public double getVelX() {
    return velX;
  }

  public void setVelX(double velX) {
    this.velX = velX;
  }

  public double getVelY() {
    return velY;
  }

  public void setVelY(double velY) {
    this.velY = velY;
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }

}
