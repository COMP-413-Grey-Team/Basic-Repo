package sprites;

import java.awt.*;

public abstract class Sprite {

  private double x;
  private double y;

  public Sprite(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public void setX(double x) {
    this.x = x;
  }

  public void setY(double y) {
    this.y = y;
  }

  public double getX() {
    return this.x;
  }

  public double getY() {
    return y;
  }

  public abstract void paint(Graphics g);


}
