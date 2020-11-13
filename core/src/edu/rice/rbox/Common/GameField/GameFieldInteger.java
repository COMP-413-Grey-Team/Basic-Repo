package edu.rice.rbox.Common.GameField;

public class GameFieldInteger implements GameField {

  private int value;

  public GameFieldInteger(int value) {
    this.value = value;
  }

  @Override
  public GameFieldInteger copy() {
    return new GameFieldInteger(value);
  }

  public int getValue() { return value; }

}