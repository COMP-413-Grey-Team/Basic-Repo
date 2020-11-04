package edu.rice.rbox.Game.Common.Utils;

import edu.rice.rbox.Protos.Generated.GameNetworkProto.CoinState;

public class CoinStateMessage {
  private CoinState.Builder coinState = CoinState.newBuilder();

  public CoinStateMessage(double x, double y) {
    coinState.setX(x);
    coinState.setY(y);
  }

  public CoinState getCoinState() {
    return coinState.build();
  }
}
