package edu.rice.rbox.Game.Common.Utils;

import network.GameNetworkProto.CoinMessage;

public class CoinStateMessage {
  private CoinMessage.Builder coin = CoinMessage.newBuilder();

  public CoinStateMessage(double x, double y) {
    coin.setX(x);
    coin.setY(y);
  }

  public CoinMessage getCoinMessage() {
    return coin.build();
  }
}
