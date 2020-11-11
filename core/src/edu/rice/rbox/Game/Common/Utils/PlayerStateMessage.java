package edu.rice.rbox.Game.Common.Utils;

import network.GameNetworkProto.PlayerMessage;

public class PlayerStateMessage {

  private final PlayerMessage.Builder state = PlayerMessage.newBuilder();

  public PlayerStateMessage(String color, String name, Integer score, Double x, Double y) {
    state.setColor(color);
    state.setName(name);
    state.setScore(score.toString());
    state.setX(x);
    state.setY(y);
  }

  public PlayerMessage getPlayerMessageMessage() {
    return this.state.build();
  }

}
