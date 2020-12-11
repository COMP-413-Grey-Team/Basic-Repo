package edu.rice.rbox.Game.Server.Messages;

import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Game.Common.SyncState.CoinState;
import edu.rice.rbox.Game.Common.SyncState.PlayerState;

import java.awt.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import network.GameNetworkProto.UpdateFromServer;
import network.GameNetworkProto.PlayerMessage;
import network.GameNetworkProto.CoinMessage;

public class UpdateFromServerMessage {

  private final UpdateFromServer.Builder update = UpdateFromServer.newBuilder();

  public UpdateFromServerMessage(Date date, String color, Map<String, PlayerState> playerStates,
                                 Map<String, CoinState> coinStates, GameObjectUUID playerUUID) {
    update.setTimestamp(date.toString());
    update.setWorldColor(color);
    update.putAllPlayerStates(this.formatPlayerStates(playerStates));
    update.putAllCoinStates(this.formatCoinStates(coinStates));
    update.setPlayerUUID(playerUUID.toString());
  }

  private Map<String, PlayerMessage> formatPlayerStates(Map<String, PlayerState> playerStates) {
    HashMap<String, PlayerMessage> formatted = new HashMap<String, PlayerMessage>();

    playerStates.entrySet().forEach(entry -> formatted.put(entry.getKey(),
        PlayerMessage.newBuilder()
           .setName(entry.getValue().name)
           .setColor(Color.BLACK.toString())
           .setScore(((Integer) entry.getValue().score).toString())
           .setX(entry.getValue().x)
           .setY(entry.getValue().y).build()));

    return formatted;
  }

  private Map<String, CoinMessage> formatCoinStates(Map<String, CoinState> coinStates) {
    HashMap<String, CoinMessage> formatted = new HashMap<String,CoinMessage>();

    coinStates.entrySet().forEach(entry -> formatted.put(entry.getKey(),
        CoinMessage.newBuilder()
            .setX(entry.getValue().x)
            .setY(entry.getValue().y).build()));

    return formatted;
  }

  public UpdateFromServer getUpdateFromServer() {
    return this.update.build();
  }


}
