package edu.rice.rbox.Game.Server;

import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Location.interest.InterestPredicate;

import java.util.HashSet;
import java.util.UUID;

public class ObjectStorageKeys {

  public static class Global {
    public static final GameObjectUUID GLOBAL_OBJ = new GameObjectUUID(new UUID(1234, 1));
    public static final String SERVER_ROOMS_MAP = "SERVER_ROOMS_MAP";
    public static final String NUMBER_OF_ROOMS = "NUMBER_OF_ROOMS";
    public static String roomKeyForIndex(int index) {
      return "ROOM_INDEX_" + index;
    }
  }

  public static class Room {
    public static final String PLAYERS_IN_ROOM = "PLAYERS_IN_ROOM";
    public static final String COINS_IN_ROOM = "COINS_IN_ROOM";
    public static final String BACKGROUND_COLOR = "BACKGROUND_COLOR";
    public static final String ROOM_INDEX = "ROOM_INDEX";
    public static final HashSet<String> IMPORTANT_FIELDS = new HashSet<>();
  }

  public static class Player {
    public static final String ROOM_ID = "ROOM_ID";
    public static final String X_POS = "X_POS";
    public static final String Y_POS = "Y_POS";
    public static final String SCORE = "SCORE";
    public static final String NAME = "NAME";
    public static final String COLOR = "COLOR";
    public static final HashSet<String> IMPORTANT_FIELDS = new HashSet<>() {{
      add(ROOM_ID);
    }};
    public static final InterestPredicate PREDICATE = null; // TODO: build predicate for players
  }

  public static class Coin {
    public static final String ROOM_ID = "ROOM_ID";
    public static final String X_POS = "X_POS";
    public static final String Y_POS = "Y_POS";
    public static final String HAS_BEEN_COLLECTED = "HAS_BEEN_COLLECTED";
    public static final HashSet<String> IMPORTANT_FIELDS = new HashSet<>() {{
      add(ROOM_ID);
    }};
  }

}
