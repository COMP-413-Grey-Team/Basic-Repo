package edu.rice.comp413fall2020grey.Common.Change;

import edu.rice.comp413fall2020grey.Common.GameObjectUUID;

import java.io.Serializable;

public abstract class Change {
    private final GameObjectUUID target;


    public Change(GameObjectUUID target) {
        this.target = target;
    }

    public GameObjectUUID getTarget() {
        return target;
    }

}