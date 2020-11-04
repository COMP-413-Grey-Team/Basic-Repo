package edu.rice.rbox.Game.Common.Utils;

import edu.rice.rbox.Protos.Generated.GameNetworkProto.CoinMessage;

public class CoinMessageMessage {
  private CoinMessage.Builder coin = CoinMessage.newBuilder();

  public CoinMessageMessage(double x, double y) {
    coin.setX(x);
    coin.setY(y);
  }

  public CoinMessage getCoinMessage() {
    return coin.build();
  }
}
