package edu.rice.rbox.Common.GameField;

public class GameFieldBoolean implements GameField {

  private boolean value;

  public GameFieldBoolean(boolean value) {
    this.value = value;
  }

  @Override
  public GameFieldBoolean copy() {
    return new GameFieldBoolean(value);
  }

  public boolean getValue() { return value; }

}
