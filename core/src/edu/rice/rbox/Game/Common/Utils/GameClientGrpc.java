package edu.rice.rbox.Game.Common.Utils;

import com.google.protobuf.Empty;
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

  public GameNetworkProto.UpdateFromServer init(String name, String color) {

    InitialPlayerStateMessage initMessage = new InitialPlayerStateMessage(name, color);

    GameNetworkProto.UpdateFromServer response =
        serverStub.initPlayer(initMessage.getInitialPlayerStateMessage());

    return response;
  }

  public GameNetworkProto.SuperPeerInfo getSuperPeer() {
    GameNetworkProto.SuperPeerInfo response =
        serverStub.getAssignedSuperPeer(Empty.newBuilder().build());

    return response;
  }

  public void remove(GameObjectUUID playerID) {
    GameNetworkProto.PlayerID.Builder idMessage = GameNetworkProto.PlayerID.newBuilder();
    serverStub.removeMe(idMessage.setPlayerID(playerID.toString()).build());
  }

  public static void main(String args[]) {
    GameClientGrpc client = new GameClientGrpc();
    client.connect(args[0]);
  }

}
