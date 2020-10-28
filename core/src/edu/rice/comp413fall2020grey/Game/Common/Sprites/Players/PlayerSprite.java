package edu.rice.comp413fall2020grey.Game.Common.Sprites.Players;

import edu.rice.comp413fall2020grey.Game.Common.Sprites.CoinSprite;
import edu.rice.comp413fall2020grey.Game.Common.Sprites.Sprite;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class PlayerSprite extends Sprite {

  public static final int CIRCLE_RADIUS = 15;
  public static final double SPEED = 0.5;

  private double velX;
  private double velY;
  private int score;

  private String name;
  private Color color;

  public PlayerSprite(Color color, double x, double y, double velX, double velY, int score, String name) {
    super(x, y);
    this.color = color;
    this.velX = velX;
    this.velY = velY;
    this.score = score;
    this.name = name;
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

  public Set<UUID> checkCoinCollisions(Map<UUID, CoinSprite> coins) {
    final Set<UUID> collected = coins.entrySet()
                                    .stream()
                                    .filter(coin -> {
                                      final CoinSprite sprite = coin.getValue();
                                      final double dx = sprite.getX() - getX();
                                      final double dy = sprite.getY() - getY();
                                      final double distance = Math.sqrt(dx * dx + dy * dy);
                                      final double
                                          collisionDistance =
                                          PlayerSprite.CIRCLE_RADIUS + CoinSprite.CIRCLE_RADIUS;
                                      return collisionDistance >= distance;
                                    })
                                    .map(Map.Entry::getKey)
                                    .collect(Collectors.toSet());
    score += collected.size();
    return collected;
  }

  public int getScore() {
    return score;
  }

  public void incrementScore() {
    score++;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
