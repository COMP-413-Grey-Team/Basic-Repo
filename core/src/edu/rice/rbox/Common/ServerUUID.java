package edu.rice.rbox.Common;

import edu.rice.rbox.Common.GameField.GameField;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class ServerUUID implements Serializable, GameField {

    private final UUID uuid;

    public ServerUUID(UUID uuid) {
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
    public boolean equals(Object ob) {
        if (ob == this) {
            return true;
        }

        if (ob == null || ob.getClass() != getClass()) {
            return false;
        }

        ServerUUID serverUUID = (ServerUUID) ob;
        return Objects.equals(serverUUID.getUUID(), uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() { return uuid.toString();}

    @Override
    public GameField copy() {
        return new ServerUUID(uuid);
    }
}
