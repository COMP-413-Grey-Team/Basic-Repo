package edu.rice.comp413fall2020grey.Common;

import java.io.Serializable;
import java.util.UUID;

public class ServerUUID implements Serializable {

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
}
