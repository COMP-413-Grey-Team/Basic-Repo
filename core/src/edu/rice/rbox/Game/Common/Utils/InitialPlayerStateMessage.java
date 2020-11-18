package edu.rice.rbox.Game.Common.Utils;

import network.GameNetworkProto.InitialPlayerState;

public class InitialPlayerStateMessage {

  private final InitialPlayerState.Builder initialState = InitialPlayerState.newBuilder();

  public InitialPlayerStateMessage(String name, String color) {
    initialState.setName(name);
    initialState.setColor(color);
  }

  public InitialPlayerState getInitialPlayerStateMessage() {
    return this.initialState.build();
  }

}
