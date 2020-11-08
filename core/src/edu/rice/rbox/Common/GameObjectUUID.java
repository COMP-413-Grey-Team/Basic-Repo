package edu.rice.rbox.Common;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class GameObjectUUID implements Serializable {

    private final UUID uuid;

    public GameObjectUUID(UUID uuid) {
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
    public boolean equals(Object ob) {
        if (ob == this) {
            return true;
        }

        if (ob == null || ob.getClass() != getClass()) {
            return false;
        }

        GameObjectUUID gameObjectUUID = (GameObjectUUID) ob;
        return Objects.equals(gameObjectUUID.getUUID(), uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
