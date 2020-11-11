package edu.rice.rbox.Game.Server;

public class ObjectStorageKeys {

  public static class Room {
    public static final String PLAYERS_IN_ROOM = "PLAYERS_IN_ROOM";
    public static final String COINS_IN_ROOM = "COINS_IN_ROOM";
  }

  public static class Player {
    public static final String ROOM_ID = "ROOM_ID";
    public static final String X_POS = "X_POS";
    public static final String Y_POS = "Y_POS";
    public static final String SCORE = "SCORE";
    public static final String NAME = "NAME";
    public static final String COLOR = "COLOR";
  }

  public static class Coin {
    public static final String ROOM_ID = "ROOM_ID";
    public static final String X_POS = "X_POS";
    public static final String Y_POS = "Y_POS";
    public static final String HAS_BEEN_COLLECTED = "HAS_BEEN_COLLECTED";
  }

}
