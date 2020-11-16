package edu.rice.rbox.Common.GameField;

public class GameFieldDouble implements GameField {

  private double value;

  public GameFieldDouble(double value) {
    this.value = value;
  }

  @Override
  public GameFieldDouble copy() {
    return new GameFieldDouble(value);
  }

  public double getValue() { return value; }

}
