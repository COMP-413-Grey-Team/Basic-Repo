package edu.rice.rbox.FaultTolerance;

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

import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

public class ConnectionManager {




    // player clients will not have gRPC servers, so we cannot have stubs to them

    private Map<RegistrarBlockingStub, List<UUID>> superPeer2gameClient;
    private Map<RegistrarBlockingStub, SuperPeerInfo> superPeers;
    private MongoCollection superPeerCol;
    private MongoCollection clientCol;


    // this is for game room assingment (each super peer is assigned a set of game rooms)
    private Map<UUID, Integer> gameRooms;
    private Integer currGameRoom;
    private Integer numGameRooms;






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


            // send over assigned superpeer info
            responseObserver.onNext(ConnectionManager.this.assignSuperPeer(UUID.fromString(request.getPlayerID())));
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
                             io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver)  {
            System.out.println("Remove me called on registrar");
            ConnectionManager.this.removeClient(UUID.fromString(request.getPlayerID()));
            com.google.protobuf.Empty empty = com.google.protobuf.Empty.newBuilder().build();
            responseObserver.onNext(empty);
            responseObserver.onCompleted();
        }
    };


    /**
     * Manager for the connections to the registrar
     * @param spCollection MongoDB collection for the super-peers
     * @param clientCollection MongoDB collection for the clients
     */
    public ConnectionManager(MongoCollection spCollection, MongoCollection clientCollection) {
        superPeers = new HashMap<>();
        this.superPeerCol = spCollection;
        this.clientCol = clientCollection;
        this.gameRooms = new HashMap<>();
    }

    /**
     * Create a stub to the Superpeer and then add the stub to the list.
     *
     * @param hostnameInfo host of the superpeer
     * @return superpeer stub
     */
    public RegistrarBlockingStub addSuperPeer(String hostnameInfo, UUID superPeerId) {
        ManagedChannel channel = ManagedChannelBuilder.forTarget(hostnameInfo)
                                     .usePlaintext(true)
                                     .build();
        RegistrarBlockingStub sp = RegistrarGrpc.newBlockingStub(channel);
        SuperPeerInfo superPeerInfo = SuperPeerInfo.newBuilder().setSuperPeerId(superPeerId.toString()).setHostname(hostnameInfo).build();
        superPeers.put(sp, superPeerInfo);
        superPeer2gameClient.put(sp, new ArrayList<>());

        Document doc = new Document("hostname", hostnameInfo).append("superPeerUUID", superPeerId.toString());
        superPeerCol.insertOne(doc);


        // assign superpeers a game room - game rooms start at 0
        if (this.currGameRoom > this.numGameRooms - 1) {
            this.currGameRoom = 0;
        }
        this.gameRooms.put(superPeerId, this.currGameRoom);


        return sp;
    }


    /**
     * Assigns a client to the superpeer with the least amount of clients assigned
     *
     * @return superPeer that is being assigned to
     */
    public SuperPeerInfo assignSuperPeer(UUID client) {
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

        Document doc = new Document("hostname", superPeers.get(min)).append("playerUUID", client.toString());
        clientCol.insertOne(doc);
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

    public static ConnectionManager buildFromMongo(MongoCollection superPeerCol, MongoCollection clientCol) {
        ConnectionManager connMan = new ConnectionManager(superPeerCol, clientCol);
        // TODO: Create connection to all of the superPeers and clients.
        return connMan;
    }

}
