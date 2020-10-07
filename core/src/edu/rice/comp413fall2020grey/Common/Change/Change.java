package edu.rice.comp413fall2020grey.Common.Change;

import edu.rice.comp413fall2020grey.Common.GameObjectUUID;

import java.io.Serializable;

public abstract class Change {
    private final GameObjectUUID target;
    private final String field;
    private final Serializable value;

    public Change(GameObjectUUID target, String field, Serializable value) {
        this.target = target;
        this.field = field;
        this.value = value;
    }

    public GameObjectUUID getTarget() {
        return target;
    }

    public String getField() {
        return field;
    }

    public Serializable getValue() {
        return value;
    }

}