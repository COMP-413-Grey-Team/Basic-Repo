package edu.rice.rbox.Game.Client.Sprites.Players;

import java.awt.*;

public class RemotePlayerSprite extends PlayerSprite {
  public RemotePlayerSprite(double x, double y, double velX, double velY, int score, String name) {
    super(Color.PINK, x, y, velX, velY, score, name);
  }

  @Override
  public void move(int time, double boardWidth, double boardHeight) {
    // TODO
  }
}


// This may be helpful later on
//  public void normalize(double speed) {
//    double totalVel = Math.abs(velX) + Math.abs(velY);
//    if (totalVel == 0)
//      return;
//    double ratio = totalVel / speed;
//    velX /= ratio;
//    velY /= ratio;
//  }