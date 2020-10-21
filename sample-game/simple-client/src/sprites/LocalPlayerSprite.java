package sprites;

import utils.KeyState;

import java.awt.*;

public class LocalPlayerSprite extends PlayerSprite {
  private final KeyState keyState;

  public LocalPlayerSprite(Color color, double x, double y, KeyState keyState) {
    super(color, x, y, keyState.horizontalMultiplier() * SPEED, keyState.verticalMultiplier() * SPEED);
    this.keyState = keyState;
  }

  public void move(int time, double boardWidth, double boardHeight) {
    this.setX(clamp(0, boardWidth, CIRCLE_RADIUS, getX() + getVelX() * ((double) time)));
    this.setY(clamp(0, boardHeight, CIRCLE_RADIUS, getY() + getVelY() * ((double) time)));
  }

  public void updateVelocity() {
    this.setVelX(keyState.horizontalMultiplier() * SPEED);
    this.setVelY(keyState.verticalMultiplier() * SPEED);
  }
}
