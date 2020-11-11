package edu.rice.rbox.Common;

import edu.rice.rbox.Common.GameField.GameField;

import java.io.Serializable;
import java.util.UUID;

public class GameObjectUUID implements Serializable, GameField {

  private final UUID uuid;

  private GameObjectUUID(UUID uuid) {
    super();
    this.uuid = uuid;
  }

  public static GameObjectUUID randomUUID() {
    return new GameObjectUUID(UUID.randomUUID());
  }

  public UUID getUUID() {
    return uuid;
  }

  @Override
  public GameField copy() {
    return new GameObjectUUID(uuid);
  }

  public static GameObjectUUID NULL = new GameObjectUUID(new UUID(0, 0));

}
