package edu.rice.rbox.Game.Common.Utils;

import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Game.Common.SyncState.PlayerState;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.HashSet;
import network.GameNetworkProto;
import network.GameServiceGrpc;

public class GameClientGrpc {

  private ManagedChannel channel;
  private GameServiceGrpc.GameServiceBlockingStub serverStub;

  public GameClientGrpc() {}

  /**
   * @param ip Expects the IP of the registrar
   *           without the corresponding port humber.
   */
  public void connect(String ip) {
    channel = ManagedChannelBuilder.forTarget(ip + ":8080")
                                       .usePlaintext(true)
                                       .build();

    serverStub = GameServiceGrpc.newBlockingStub(channel);
  }

  public GameNetworkProto.UpdateFromServer update(GameObjectUUID objectID, PlayerState playerState,
                     HashSet<GameObjectUUID> deletedCoins, Integer movingRooms) {
    UpdateFromClientMessage updateMessage = new UpdateFromClientMessage(objectID, playerState,
        deletedCoins, movingRooms);

    GameNetworkProto.UpdateFromServer response =
        serverStub.publishUpdate(updateMessage.getUpdateFromClientMessage());

    return response;
  }

  public static void main(String args[]) {
    GameClientGrpc client = new GameClientGrpc();
    client.connect(args[0]);
  }

}
