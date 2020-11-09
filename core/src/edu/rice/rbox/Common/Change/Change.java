package edu.rice.rbox.Common.Change;

import edu.rice.rbox.Common.GameObjectUUID;

public abstract class Change {
    private final GameObjectUUID target;


    public Change(GameObjectUUID target) {
        this.target = target;
    }

    public GameObjectUUID getTarget() {
        return target;
    }

}