//package edu.rice.rbox.Game.Common.Utils;
//
//import io.grpc.stub.StreamObserver;
//import network.GameNetworkProto;
//import network.GameServiceGrpc.GameServiceImplBase;
//
//public class GameGrpc extends GameServiceImplBase {
//
//
//  @Override
//  public void publishUpdate(GameNetworkProto.UpdateFromClient request,
//                            StreamObserver<GameNetworkProto.UpdateFromServer> responseObserver) {
//    super.publishUpdate(request, responseObserver);
//  }
//
//  @Override
//  public void getAssignedSuperPeer(GameNetworkProto.Empty request,
//                                   StreamObserver<GameNetworkProto.SuperPeerInfo> responseObserver) {
//    super.getAssignedSuperPeer(request, responseObserver);
//  }
//
//  @Override
//  public void initPlayer(GameNetworkProto.InitialPlayerState request,
//                         StreamObserver<GameNetworkProto.UpdateFromServer> responseObserver) {
//    super.initPlayer(request, responseObserver);
//  }
//
//  @Override
//  public void removeMe(GameNetworkProto.PlayerID request, StreamObserver<GameNetworkProto.Empty> responseObserver) {
//    super.removeMe(request, responseObserver);
//  }
//
//}
