package edu.rice.rbox.Game.Client.Sprites;

import java.awt.*;

public class DoorSprite extends Sprite {

  public static final int HEIGHT = 60;
  public static final int WIDTH = 42;
  public static final Color DOOR_COLOR = new Color(139, 69, 19);

  public enum DoorSide {
    LEFT, RIGHT;
  }

  public final DoorSide doorSide;

  public DoorSprite(DoorSide doorSide, int boardWidth, int boardHeight) {
    super(doorSide == DoorSide.LEFT ? WIDTH : boardWidth - WIDTH, boardHeight / 2.0);
    this.doorSide = doorSide;
  }

  @Override
  public void paint(Graphics g) {
    g.setColor(DOOR_COLOR);
    g.fillRect((int)(getX() - WIDTH / 2), (int)(getY() - HEIGHT / 2), WIDTH, HEIGHT);
  }

  public boolean containsPoint(double x, double y) {
    double minX = this.getX() - WIDTH / 2.0;
    double maxX = this.getX() + WIDTH / 2.0;
    double minY = this.getY() - HEIGHT / 2.0;
    double maxY = this.getY() + HEIGHT / 2.0;
    return minX <= x && maxX >= x && minY <= y && maxY >= y;
  }

}
