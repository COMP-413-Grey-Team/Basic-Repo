package edu.rice.rbox.Game.Client;

import com.google.protobuf.Empty;
import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Game.Common.SyncState.CoinState;
import edu.rice.rbox.Game.Common.SyncState.GameState;
import edu.rice.rbox.Game.Common.SyncState.GameStateDelta;
import edu.rice.rbox.Game.Common.SyncState.PlayerState;
import edu.rice.rbox.Game.Client.Messages.UpdateFromClientMessage;
import edu.rice.rbox.Game.Server.Messages.UpdateFromServerMessage;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.awt.*;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;

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

    GameNetworkProto.InitialPlayerState.Builder initMessage =
        GameNetworkProto.InitialPlayerState.newBuilder();

    GameNetworkProto.UpdateFromServer response =
        serverStub.initPlayer(initMessage.setName(name).setColor(color).build());

    return response;
  }

  // TODO: This message should be sent to the registrar, not the game server.
  public GameNetworkProto.SuperPeerInfo getSuperPeer() {
    GameNetworkProto.SuperPeerInfo response =
        serverStub.getAssignedSuperPeer(Empty.newBuilder().build());

    return response;
  }

  public void remove(GameObjectUUID playerID) {
    GameNetworkProto.PlayerID.Builder idMessage = GameNetworkProto.PlayerID.newBuilder();
    serverStub.removeMe(idMessage.setPlayerID(playerID.toString()).build());
  }

  private  PlayerState reconstructPlayerState(GameNetworkProto.PlayerMessage msg) {
    return new PlayerState(msg.getX(), msg.getY(),
            msg.getName(), new Color(Integer.parseInt(msg.getColor())),
            Integer.parseInt(msg.getScore()));
  }

  public GameState serverMsgToGameState(UpdateFromServerMessage msg) {
    GameNetworkProto.UpdateFromServer update = msg.getUpdateFromServer();
    return new GameState(new GameObjectUUID(UUID.fromString(update.getPlayerUUID())),
            update.getPlayerStatesMap().entrySet().stream().collect(Collectors.toMap(
                    e -> new GameObjectUUID(UUID.fromString(e.getKey())),
                    e -> reconstructPlayerState(e.getValue()))),
            update.getCoinStatesMap().entrySet().stream().collect(Collectors.toMap(
                    e -> new GameObjectUUID(UUID.fromString(e.getKey())),
                    e -> new CoinState(e.getValue().getX(), e.getValue().getY()))),
            new Color(Integer.parseInt(update.getWorldColor())));
  }

  public UpdateFromClientMessage gameStateDeltaToClientMsg(GameStateDelta gsd) {
    return new UpdateFromClientMessage(gsd.playerUUID, gsd.updatedPlayerState,
            gsd.deletedCoins,
            gsd.movingRooms.getNumber());
  }

  public static void main(String args[]) {
    GameClientGrpc client = new GameClientGrpc();
    client.connect(args[0]);
  }

}
