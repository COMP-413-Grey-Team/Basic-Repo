package edu.rice.rbox.Game.Server;

import edu.rice.rbox.Common.GameField;

public class GameFieldBoolean implements GameField {

  boolean value;

  public GameFieldBoolean(boolean value) {
    this.value = value;
  }

  @Override
  public GameField copy() {
    return new GameFieldBoolean(value);
  }

}
