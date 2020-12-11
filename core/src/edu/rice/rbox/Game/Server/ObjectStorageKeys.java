package edu.rice.rbox.Game.Server;

import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Common.ServerUUID;
import edu.rice.rbox.Location.interest.InterestPredicate;

import java.util.HashSet;
import java.util.UUID;

public class ObjectStorageKeys {

  public static final String TYPE = "TYPE";

  public static class Room {
    public static final String TYPE_NAME = "ROOM";
    public static final String PLAYERS_IN_ROOM = "PLAYERS_IN_ROOM";
    public static final String COINS_IN_ROOM = "COINS_IN_ROOM";
    public static final String BACKGROUND_COLOR = "BACKGROUND_COLOR";
    public static final HashSet<String> IMPORTANT_FIELDS = new HashSet<>() {{
      add(TYPE);
    }};
  }

  public static class Player {
    public static final String TYPE_NAME = "PLAYER";
    public static final String ROOM_ID = "ROOM_ID";
    public static final String X_POS = "X_POS";
    public static final String Y_POS = "Y_POS";
    public static final String COIN_COUNT = "COIN_COUNT";
    public static final String NAME = "NAME";
    public static final HashSet<String> IMPORTANT_FIELDS = new HashSet<>() {{
      add(TYPE);
      add(ROOM_ID);
    }};
    public static final InterestPredicate PREDICATE = null; // TODO: build predicate for players
  }

  public static class Coin {
    public static final String TYPE_NAME = "COIN";
    public static final String ROOM_ID = "ROOM_ID";
    public static final String X_POS = "X_POS";
    public static final String Y_POS = "Y_POS";
    public static final String HAS_BEEN_COLLECTED = "HAS_BEEN_COLLECTED";
    public static final HashSet<String> IMPORTANT_FIELDS = new HashSet<>() {{
      add(TYPE);
      add(ROOM_ID);
    }};
  }

}
