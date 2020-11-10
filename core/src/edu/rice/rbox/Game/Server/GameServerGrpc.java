package edu.rice.rbox.Game.Server;

import com.google.protobuf.Empty;
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

public class GameServerGrpc extends GameServiceImplBase {

  public GameServerGrpc() {
  }


  @Override
  public void publishUpdate(GameNetworkProto.UpdateFromClient request,
                            StreamObserver<GameNetworkProto.UpdateFromServer> responseObserver) {
    System.out.println("Game Object ID: " + request.getGameObjectUUID());
  }

  @Override
  public void getAssignedSuperPeer(Empty request,
                                   StreamObserver<GameNetworkProto.SuperPeerInfo> responseObserver) {
  }

  @Override
  public void initPlayer(GameNetworkProto.InitialPlayerState request,
                         StreamObserver<GameNetworkProto.UpdateFromServer> responseObserver) {
    System.out.println("Name: " + request.getName());
    System.out.println("Color: " + request.getColor());
  }

  @Override
  public void removeMe(GameNetworkProto.PlayerID request, StreamObserver<Empty> responseObserver) {
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
