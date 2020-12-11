package edu.rice.rbox.Game.Client;

import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Game.Common.SyncState.GameStateDelta;
import network.GameNetworkProto;

public interface GameClient {

  void connect(String ip);

  GameNetworkProto.UpdateFromServer update(GameStateDelta gsd);

  GameNetworkProto.UpdateFromServer init(String name, String color);

  GameNetworkProto.SuperPeerInfo getSuperPeer(String playerID);

  void remove(GameObjectUUID playerID);


}
