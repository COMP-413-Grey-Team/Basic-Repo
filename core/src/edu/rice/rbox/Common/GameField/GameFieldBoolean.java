package edu.rice.rbox.Common.GameField;

public class GameFieldBoolean implements InterestingGameField<Boolean> {

  private boolean value;

  public GameFieldBoolean(boolean value) {
    this.value = value;
  }

  @Override
  public GameFieldBoolean copy() {
    return new GameFieldBoolean(value);
  }

  @Override
  public Boolean getValue() { return value; }

}
