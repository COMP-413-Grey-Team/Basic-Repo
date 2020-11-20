package edu.rice.rbox.Common;

import edu.rice.rbox.Common.GameField.GameField;

import java.io.Serializable;
import java.util.UUID;

public class ServerUUID implements Serializable, GameField {

  private final UUID uuid;

  private ServerUUID(UUID uuid) {
    super();
    this.uuid = uuid;
  }

  public static ServerUUID randomUUID() {
    return new ServerUUID(UUID.randomUUID());
  }

  public UUID getUUID() {
    return uuid;
  }

  @Override
  public GameField copy() {
    return new ServerUUID(uuid);
  }
}
