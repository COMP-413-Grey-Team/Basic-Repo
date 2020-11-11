package edu.rice.rbox.Common.GameField;

public class GameFieldString implements GameField {

  private String value;

  public GameFieldString(String value) {
    this.value = value;
  }

  @Override
  public GameFieldString copy() {
    return new GameFieldString(value);
  }

  public String getValue() { return value; }

}