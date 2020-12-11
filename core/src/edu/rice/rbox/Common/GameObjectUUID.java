package edu.rice.rbox.Common;

import edu.rice.rbox.Common.GameField.GameField;
import edu.rice.rbox.Common.GameField.InterestingGameField;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class GameObjectUUID implements Serializable, InterestingGameField<Void> {

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
        return uuid.hashCode();
    }

    @Override
    public String toString() { return uuid.toString();}

    @Override
    public GameField copy() {
        return new GameObjectUUID(uuid);
    }

    public static GameObjectUUID NULL = new GameObjectUUID(new UUID(0, 0));

    @Override
    public Void getValue() {
        return null;
    }
}
