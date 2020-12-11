package edu.rice.rbox.Game.Client;

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
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

import network.GameNetworkProto;
import network.GameServiceGrpc;


// TODO: Handle player reconnecting when the server goes down?
public class GameClientGrpc {

  private ManagedChannel channel;
  private GameServiceGrpc.GameServiceBlockingStub registrarStub;
  private GameServiceGrpc.GameServiceBlockingStub gamerServerStub;

  public GameClientGrpc() {}

  /**
   * @param ip Expects the IP of the registrar
   *           without the corresponding port humber.
   */
  public void connect(String ip) {
    channel = ManagedChannelBuilder.forTarget(ip + ":8080")
                                       .usePlaintext(true)
                                       .build();

    registrarStub = GameServiceGrpc.newBlockingStub(channel);
  }

  /**
   * This is sent to the game server.
   */
  public GameState update(GameStateDelta gsd) {
    UpdateFromClientMessage updateMessage = this.gameStateDeltaToClientMsg(gsd);

    GameNetworkProto.UpdateFromServer response =
        gamerServerStub.publishUpdate(updateMessage.getUpdateFromClientMessage());

    return this.serverMsgToGameState(response);
  }

  /**
   * This is sent to the game server.
   */
  public GameState init(String name, String color) {

    GameNetworkProto.InitialPlayerState.Builder initMessage =
        GameNetworkProto.InitialPlayerState.newBuilder();

    GameNetworkProto.UpdateFromServer response =
        gamerServerStub.initPlayer(initMessage.setName(name).setColor(color).build());

    return this.serverMsgToGameState(response);
  }

  /**
   * This is sent to the registrar.
   */
  public GameNetworkProto.SuperPeerInfo getSuperPeer(String playerID) {
    GameNetworkProto.SuperPeerInfo response =
        registrarStub.getAssignedSuperPeer(GameNetworkProto.PlayerID.newBuilder().setPlayerID(playerID).build());

    System.out.println("This was called!");
    // This is the UUID = 2dfa71ec-4df8-48f8-b7fe-202e2994fd50
    System.out.println("This is the super peer's id: " + response.getSuperPeerId());
    // This is the IP = 10.125.200.165:3000
    System.out.println("This is the super peer host name: " + response.getHostname());

    // Connect to the super peer here.
    ManagedChannel channel = ManagedChannelBuilder.forTarget(response.getHostname())
                                 .usePlaintext(true)
                                 .build();
    this.gamerServerStub = GameServiceGrpc.newBlockingStub(channel);

    return response;
  }

  /**
   * This goes to the game server.
   */
  public void remove(GameObjectUUID playerID) {
    GameNetworkProto.PlayerID.Builder idMessage = GameNetworkProto.PlayerID.newBuilder();
    this.gamerServerStub.removeMe(idMessage.setPlayerID(playerID.toString()).build());

    System.out.println("This message should have been set!");
  }

  /**
   * This is a helper.
   */
  private  PlayerState reconstructPlayerState(GameNetworkProto.PlayerMessage msg) {
    return new PlayerState(msg.getX(), msg.getY(),
            msg.getName(), new Color(Integer.parseInt(msg.getColor())),
            Integer.parseInt(msg.getScore()));
  }

  /**
   * This is a helper.
   */
  public GameState serverMsgToGameState(GameNetworkProto.UpdateFromServer update) {

    return new GameState(new GameObjectUUID(UUID.fromString(update.getPlayerUUID())),
            update.getPlayerStatesMap().entrySet().stream().collect(Collectors.toMap(
                    e -> new GameObjectUUID(UUID.fromString(e.getKey())),
                    e -> reconstructPlayerState(e.getValue()))),
            update.getCoinStatesMap().entrySet().stream().collect(Collectors.toMap(
                    e -> new GameObjectUUID(UUID.fromString(e.getKey())),
                    e -> new CoinState(e.getValue().getX(), e.getValue().getY()))),
            new Color(Integer.parseInt(update.getWorldColor())));
  }

  /**
   * This is a seemingly unused helper.
   */
  public UpdateFromClientMessage gameStateDeltaToClientMsg(GameStateDelta gsd) {
    return new UpdateFromClientMessage(gsd.playerUUID, gsd.updatedPlayerState,
            gsd.deletedCoins,
            gsd.movingRooms.getNumber());
  }

}
