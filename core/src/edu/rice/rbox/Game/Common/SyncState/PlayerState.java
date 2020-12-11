package edu.rice.rbox.Game.Common.SyncState;

import java.awt.*;

public class PlayerState extends SpriteState {

  public final String name;
  public final int score;

  public PlayerState(double x, double y, String name, int score) {
    super(x, y);
    this.name = name;
    this.score = score;
  }
}