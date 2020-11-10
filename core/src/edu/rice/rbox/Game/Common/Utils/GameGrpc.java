package edu.rice.rbox.Game.Common.Utils;

import edu.rice.rbox.Networking.NetworkImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import network.GameNetworkProto;
import network.GameServiceGrpc.GameServiceImplBase;

public class GameGrpc extends GameServiceImplBase {

  public GameGrpc() {
  }


  @Override
  public void publishUpdate(GameNetworkProto.UpdateFromClient request,
                            StreamObserver<GameNetworkProto.UpdateFromServer> responseObserver) {
    System.out.println("Game Object ID: " + request.getGameObjectUUID());
  }

  @Override
  public void getAssignedSuperPeer(GameNetworkProto.Empty request,
                                   StreamObserver<GameNetworkProto.SuperPeerInfo> responseObserver) {
  }

  @Override
  public void initPlayer(GameNetworkProto.InitialPlayerState request,
                         StreamObserver<GameNetworkProto.UpdateFromServer> responseObserver) {
    System.out.println("Name: " + request.getName());
    System.out.println("Color: " + request.getColor());
  }

  @Override
  public void removeMe(GameNetworkProto.PlayerID request, StreamObserver<GameNetworkProto.Empty> responseObserver) {
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
                        .addService(new GameGrpc())
                        .build();


    // Start the server
    server.start();

    // Server threads are running in the background.
    System.out.println("Server started");

    // Don't exit the main thread. Wait until server is terminated.
    server.awaitTermination();
  }

}
