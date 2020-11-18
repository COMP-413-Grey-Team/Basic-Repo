package edu.rice.rbox.Location.registrar;


import com.mongodb.client.MongoCollection;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.*;

import network.GameNetworkProto;
import network.GameServiceGrpc;
import network.GameServiceGrpc.GameServiceBlockingStub;
import network.GameNetworkProto.SuperPeerInfo;
import network.RegistrarGrpc;
import network.RegistrarGrpc.RegistrarBlockingStub;
import org.bson.Document;

public class TheCoolConnectionManager {




  // player clients will not have gRPC servers, so we cannot have stubs to them
  private List<GameServiceBlockingStub> clients;

  private Map<RegistrarBlockingStub, List<UUID>> superPeer2gameClient;
  private Map<RegistrarBlockingStub, String> superPeers;
  private MongoCollection superPeerCol;
  private MongoCollection clientCol;






  private GameServiceGrpc.GameServiceImplBase gameServerRegistrarImpl = new GameServiceGrpc.GameServiceImplBase() {
    @Override
    public void publishUpdate(network.GameNetworkProto.UpdateFromClient request,
                              io.grpc.stub.StreamObserver<network.GameNetworkProto.UpdateFromServer> responseObserver) {

      System.out.println("Publish Update incorrectly called on registrar!!!");
      GameNetworkProto.UpdateFromServer empty = GameNetworkProto.UpdateFromServer.newBuilder().build();
      responseObserver.onNext(empty);
      responseObserver.onCompleted();
    }

    @Override
    public void getAssignedSuperPeer(GameNetworkProto.PlayerID request,
                                     io.grpc.stub.StreamObserver<network.GameNetworkProto.SuperPeerInfo> responseObserver) {
      System.out.println("Get Assign incorrectly called on registrar!");

      // Need client UUID
      String sp = TheCoolConnectionManager.this.assignClient(UUID.fromString(request.getPlayerID()));

      // send over assigned superpeer info
      SuperPeerInfo info = SuperPeerInfo.newBuilder().setHostname(sp).build();
      responseObserver.onNext(info);
      responseObserver.onCompleted();


    }

    @Override
    public void initPlayer(network.GameNetworkProto.InitialPlayerState request,
                           io.grpc.stub.StreamObserver<network.GameNetworkProto.UpdateFromServer> responseObserver) {
      System.out.println("Init Player incorrectly called on registrar!!!");
      GameNetworkProto.UpdateFromServer empty = GameNetworkProto.UpdateFromServer.newBuilder().build();
      responseObserver.onNext(empty);
      responseObserver.onCompleted();
    }

    @Override
    public void removeMe(network.GameNetworkProto.PlayerID request,
                         io.grpc.stub.StreamObserver<network.GameNetworkProto.Empty> responseObserver) {
      System.out.println("Remove me called on registrar");
      TheCoolConnectionManager.this.removeClient(UUID.fromString(request.getPlayerID()));
      network.GameNetworkProto.Empty empty = network.GameNetworkProto.Empty.newBuilder().build();
      responseObserver.onNext(empty);
      responseObserver.onCompleted();
    }
  };
  // define the gameservice server in connection manager


  /**
   * Manager for the connections to the registrar
   * @param spCollection MongoDB collection for the super-peers
   * @param clientCollection MongoDB collection for the clients
   */
  public TheCoolConnectionManager(MongoCollection spCollection, MongoCollection clientCollection) {
    clients = new ArrayList<>();
    superPeers = new HashMap<>();
    this.superPeerCol = spCollection;
    this.clientCol = clientCollection;

  }

  /**
   * Create a stub to the Superpeer and then add the stub to the list.
   *
   * @param hostnameInfo host of the superpeer
   * @return superpeer stub
   */
  public RegistrarBlockingStub addSuperPeer(String hostnameInfo) {
    ManagedChannel channel = ManagedChannelBuilder.forTarget(hostnameInfo)
                                 .usePlaintext(true)
                                 .build();
    RegistrarBlockingStub sp = RegistrarGrpc.newBlockingStub(channel);
    superPeers.put(sp, hostnameInfo);
    superPeer2gameClient.put(sp, new ArrayList<>());

    Document doc = new Document("hostname", hostnameInfo);
    superPeerCol.insertOne(doc);

    return sp;
  }

  /**
   * Create a stub to the client and add the stub to the list
   *
   * @param hostname host of the client
   * @param port port of the client
   * @return client stub
   */
  public String addClient(String hostname, String port) {

    Document doc = new Document("hostname", hostname).append("port", port);
    clientCol.insertOne(doc);

    return hostname + ":" + port;
  }

  /**
   * Assigns a client to the superpeer with the least amount of clients assigned
   *
   * @return superPeer that is being assigned to
   */
  public String assignClient(UUID client) {
    RegistrarBlockingStub min = null;
    int minVal = -1;

    for(Map.Entry<RegistrarBlockingStub, List<UUID>> e : superPeer2gameClient.entrySet()) {
      if (minVal == -1 || e.getValue().size() < minVal){
        min = e.getKey();
        minVal = e.getValue().size();
      }
      if (minVal == 0) {
        break;
      }
    }

    superPeer2gameClient.get(min).add(client);
    return superPeers.get(min);
  }

  public void removeClient(UUID client) {


    for(Map.Entry<RegistrarBlockingStub, List<UUID>> e : superPeer2gameClient.entrySet()) {
      if (e.getValue().contains(client)){
        e.getValue().remove(client);
        break;
      }
    }
  }

  public GameServiceGrpc.GameServiceImplBase getGameServerRegistrarImpl(){
    return gameServerRegistrarImpl;
  }

}

