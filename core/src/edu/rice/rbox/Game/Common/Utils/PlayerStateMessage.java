package edu.rice.rbox.Game.Common.Utils;

import edu.rice.rbox.Protos.Generated.GameNetworkProto.PlayerState;

public class PlayerStateMessage {

  private final PlayerState.Builder state = PlayerState.newBuilder();

  public PlayerStateMessage(String color, String name, Integer score, Double x, Double y) {
    state.setColor(color);
    state.setName(name);
    state.setScore(score.toString());
    state.setX(x);
    state.setY(y);
  }

  public PlayerState getPlayerStateMessage() {
    return this.state.build();
  }

}
