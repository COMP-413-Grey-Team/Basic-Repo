package edu.rice.comp413fall2020grey.Replication;

import edu.rice.comp413fall2020grey.Common.GameObjectUUID;

import java.net.InetAddress;

public class HolderInfo {

    private final GameObjectUUID gameObjUUID;
    private final InetAddress superpeerIP;

    public HolderInfo (GameObjectUUID gameObjUUID, InetAddress superpeerIP) {
        this.gameObjUUID = gameObjUUID;
        this.superpeerIP = superpeerIP;
    }

    public GameObjectUUID getGameObjUUID() { return gameObjUUID; }

    public InetAddress getSuperpeerIP() { return superpeerIP; }

}
