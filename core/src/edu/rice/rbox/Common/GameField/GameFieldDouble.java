package edu.rice.rbox.Common.GameField;

public class GameFieldDouble implements InterestingGameField<Double> {

  private Double value;

  public GameFieldDouble(double value) {
    this.value = value;
  }

  @Override
  public GameFieldDouble copy() {
    return new GameFieldDouble(value);
  }

  public Double getValue() { return value; }

}
