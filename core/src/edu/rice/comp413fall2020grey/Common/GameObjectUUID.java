package edu.rice.comp413fall2020grey.Common;

import java.io.Serializable;
import java.util.UUID;

public class GameObjectUUID implements Serializable {

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
}
