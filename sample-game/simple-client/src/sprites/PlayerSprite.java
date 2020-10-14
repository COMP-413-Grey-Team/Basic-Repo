package sprites;

import java.awt.*;

public class PlayerSprite extends Sprite {

  private final int CIRCLE_RADIUS = 15;
  private Color color;
  private double playerVelX = 0.0;
  private double playerVelY = 0.0;

  public PlayerSprite(double x, double y) {
    super(x, y);
    this.color = Color.ORANGE;
  }

  public void setPlayerVelX(double playerVelX) {
    this.playerVelX = playerVelX;
  }

  public void setPlayerVelY(double playerVelY) {
    this.playerVelY = playerVelY;
  }

  @Override
  public void paint(Graphics g) {
    g.setColor(this.color);
    g.fillOval((int) this.getX() - CIRCLE_RADIUS, (int) this.getY() - CIRCLE_RADIUS,
        CIRCLE_RADIUS * 2, CIRCLE_RADIUS * 2);
  }

  public void updateState(int width, int time) {
    this.move(width, time);
  }

  public void move(int width, int time) {
    this.setX(this.getX() + playerVelX * ((double) time / width));
    System.out.println("Player x loc: " + this.getX());
    this.setY(this.getY() + playerVelY * ((double) time / width));
  }

}
