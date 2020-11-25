package edu.rice.rbox.Common.GameField;

import java.awt.*;

public class GameFieldColor implements GameField {

  private Color value;

  public GameFieldColor(Color value) {
    this.value = value;
  }

  @Override
  public GameFieldColor copy() {
    return new GameFieldColor(value);
  }

  public Color getValue() { return value; }

}