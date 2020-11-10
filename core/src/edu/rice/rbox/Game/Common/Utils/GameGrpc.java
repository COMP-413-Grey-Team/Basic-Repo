package edu.rice.rbox.Game.Common.Utils;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
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

}
