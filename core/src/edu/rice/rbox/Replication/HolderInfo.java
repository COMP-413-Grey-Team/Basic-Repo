package edu.rice.rbox.Replication;

import edu.rice.rbox.Common.ServerUUID;
import edu.rice.rbox.Common.GameObjectUUID;

public class HolderInfo {

    private final GameObjectUUID gameObjectUUID;
    private final ServerUUID serverUUID;

    public HolderInfo (GameObjectUUID gameObjectUUID, ServerUUID serverUUID) {
        this.gameObjectUUID = gameObjectUUID;
        this.serverUUID = serverUUID;
    }

    public GameObjectUUID getGameObjectUUID() { return gameObjectUUID; }

    public ServerUUID getServerUUID() { return serverUUID; }

}
