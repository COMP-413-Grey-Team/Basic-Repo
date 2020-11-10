package edu.rice.rbox.Game.Common.Utils;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GameClientGrpc {

  private ManagedChannel channel;

  public GameClientGrpc() {}

  public void connect() {
    channel = ManagedChannelBuilder.forTarget("2600:1700:1112:c190:f5fb:979c:d162:d827")
                                       .usePlaintext(true)
                                       .build();
  }

}
