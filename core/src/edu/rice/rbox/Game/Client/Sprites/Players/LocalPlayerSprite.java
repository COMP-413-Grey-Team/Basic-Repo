package edu.rice.rbox.Game.Client.Sprites.Players;

import edu.rice.rbox.Game.Common.SyncState.PlayerState;
import edu.rice.rbox.Game.Common.Utils.KeyState;

import java.awt.*;

public class LocalPlayerSprite extends PlayerSprite {
  private final KeyState keyState;

  public LocalPlayerSprite(double x, double y, int score, String name, KeyState keyState) {
    super(Color.BLACK, x, y, keyState.horizontalMultiplier() * SPEED, keyState.verticalMultiplier() * SPEED, score, name);
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

  public PlayerState getPlayerState() {
    return new PlayerState(getX(), getY(), getName(), getScore());
  }
}
