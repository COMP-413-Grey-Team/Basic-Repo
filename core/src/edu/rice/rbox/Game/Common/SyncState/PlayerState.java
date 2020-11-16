package edu.rice.rbox.Game.Common.SyncState;

import java.awt.*;

public class PlayerState extends SpriteState {

  public final String name;
  public final Color color;
  public final int score;

  public PlayerState(double x, double y, String name, Color color, int score) {
    super(x, y);
    this.name = name;
    this.color = color;
    this.score = score;
  }
}