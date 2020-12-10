package edu.rice.rbox.Common.GameField;

public class GameFieldInteger implements InterestingGameField<Integer> {

  private Integer value;

  public GameFieldInteger(int value) {
    this.value = value;
  }

  @Override
  public GameFieldInteger copy() {
    return new GameFieldInteger(value);
  }

  public Integer getValue() { return value; }

}