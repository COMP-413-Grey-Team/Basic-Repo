package edu.rice.rbox.Game.Server;

import com.google.protobuf.Empty;
import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Game.Client.Messages.UpdateFromClientMessage;
import edu.rice.rbox.Game.Common.SyncState.GameState;
import edu.rice.rbox.Game.Common.SyncState.GameStateDelta;
import edu.rice.rbox.Game.Common.SyncState.PlayerState;
import edu.rice.rbox.Game.Server.Messages.UpdateFromServerMessage;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.awt.*;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import network.GameNetworkProto;
import network.GameServiceGrpc.GameServiceImplBase;

public class GameServerGrpc extends GameServiceImplBase {

  public GameServerGrpc() {
  }


  @Override
  public void publishUpdate(GameNetworkProto.UpdateFromClient request,
                            StreamObserver<GameNetworkProto.UpdateFromServer> responseObserver) {
    System.out.println("Game Object ID: " + request.getGameObjectUUID());
    System.out.println("Deleted Coins: " + request.getDeletedCoinsList().toString());
    System.out.println("Player State: " + this.reconstructPlayerState(request).toString());
    System.out.println("Moved Rooms?: " + (((Integer) request.getMovingRoomsValue())).toString());

    responseObserver.onNext(GameNetworkProto.UpdateFromServer.newBuilder().build());
    responseObserver.onCompleted();
  }

  private PlayerState reconstructPlayerState(GameNetworkProto.UpdateFromClient request) {
    return new PlayerState(request.getPlayerState().getX(), request.getPlayerState().getY(),
        request.getPlayerState().getName(), new Color(Integer.parseInt(request.getPlayerState().getColor())),
        Integer.parseInt(request.getPlayerState().getScore()));
  }

  @Override
  public void initPlayer(GameNetworkProto.InitialPlayerState request,
                         StreamObserver<GameNetworkProto.UpdateFromServer> responseObserver) {
    System.out.println("Name: " + request.getName());
    System.out.println("Color: " + request.getColor());

    responseObserver.onNext(GameNetworkProto.UpdateFromServer.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  public void removeMe(GameNetworkProto.PlayerID request, StreamObserver<Empty> responseObserver) {
    System.out.println("Player ID: " + request.getPlayerID());

    responseObserver.onNext(Empty.newBuilder().build());
    responseObserver.onCompleted();
  }

  public UpdateFromServerMessage gameStateToServerMsg(GameState gs) {
    return new UpdateFromServerMessage(new Date(), String.valueOf(gs.backgroundColor.getRGB()),
            gs.playerStates.entrySet().stream()
                    .collect(Collectors.toMap(e -> e.getKey().toString(), Map.Entry::getValue)),
            gs.coinStates.entrySet().stream()
                    .collect(Collectors.toMap(e -> e.getKey().toString(), Map.Entry::getValue)),
            gs.clientUUID);
  }

  public GameStateDelta clientMsgToGameStateDelta(UpdateFromClientMessage msg) {
    GameNetworkProto.UpdateFromClient update = msg.getUpdateFromClientMessage();
    HashSet<GameObjectUUID> delCoins = new HashSet<GameObjectUUID>();
    for (String dc: update.getDeletedCoinsList()) {
      delCoins.add(new GameObjectUUID(UUID.fromString(dc)));
    }
    return new GameStateDelta(new GameObjectUUID(UUID.fromString(update.getGameObjectUUID())),
            reconstructPlayerState(update), delCoins, update.getMovingRooms());
  }

  public static void main(String args[]) throws IOException, InterruptedException {
    String ip = "";
    try(final DatagramSocket socket = new DatagramSocket()){
      socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
      ip = socket.getLocalAddress().getHostAddress();
    } catch (SocketException | UnknownHostException e) {
      System.err.println("Server failed to start.");
      e.printStackTrace();
    }

    System.out.println("Server Running on address: " + ip);


    // Create a new server to listen on port 8080

    Server server = ServerBuilder.forPort(8080)
                        .addService(new GameServerGrpc())
                        .build();


    // Start the server
    server.start();

    // Server threads are running in the background.
    System.out.println("Server started");

    // Don't exit the main thread. Wait until server is terminated.
    server.awaitTermination();
  }

}
