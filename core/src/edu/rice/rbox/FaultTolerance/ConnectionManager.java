package edu.rice.rbox.FaultTolerance;

import com.google.protobuf.Timestamp;
import com.mongodb.client.MongoCollection;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.*;

import network.*;
import network.GameNetworkProto.SuperPeerInfo;
import network.RegistrarGrpc.RegistrarBlockingStub;
import org.bson.Document;

public class ConnectionManager {

    // player clients will not have gRPC servers, so we cannot have stubs to them

    private Map<RegistrarBlockingStub, List<UUID>> superPeer2gameClient;
    private Map<RegistrarBlockingStub, SuperPeerInfo> superPeers;
    private MongoCollection superPeerCol;
    private MongoCollection clientCol;


    // this is for game room assingment (each super peer is assigned a set of game rooms)
    private Map<RegistrarBlockingStub, List<Integer>> stub2Room;
    private Integer currGameRoom;


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
        this.stub2Room = new HashMap<>();
        this.superPeer2gameClient = new HashMap<>();
    }

    /**
     * Create a stub to the Superpeer and then add the stub to the list.
     *
     * @param hostnameInfo host of the superpeer
     * @return superpeer stub
     */
    public RegistrarBlockingStub addSuperPeer(String hostnameInfo, UUID superPeerId) {
        System.out.println("trying to create a superpeer stub to " + hostnameInfo);
        System.out.println("Super Peer " + hostnameInfo + " connecting");
        ManagedChannel channel = ManagedChannelBuilder.forTarget(hostnameInfo)
                                     .usePlaintext(true)
                                     .build();
        RegistrarBlockingStub sp = RegistrarGrpc.newBlockingStub(channel);
        SuperPeerInfo superPeerInfo = SuperPeerInfo.newBuilder().setSuperPeerId(superPeerId.toString()).setHostname(hostnameInfo).build();
        superPeers.put(sp, superPeerInfo);
        superPeer2gameClient.put(sp, new ArrayList<>());

        Document doc = new Document("hostname", hostnameInfo).append("superPeerUUID", superPeerId.toString());
        superPeerCol.insertOne(doc);
        stub2Room.put(sp, new ArrayList<>());
        if (stub2Room.size() >= 3) {
            // TODO: Fix numbers
            assignRoomsToSuperPeers(5);
        }


//        // assign superpeers a game room - game rooms start at 0
//        if (this.currGameRoom > this.numGameRooms - 1) {
//            this.currGameRoom = 0;
//        }
//        this.gameRooms.put(superPeerId, this.currGameRoom);


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

    public Map<RegistrarBlockingStub, List<Integer>> assignRoomsToSuperPeers(int numRooms) {
        int currAssigned = 0;
        if  (stub2Room.size() < 3){
            return null;
        }


        for (Map.Entry<RegistrarBlockingStub, List<Integer>> e: stub2Room.entrySet()) {
            if (numRooms > currAssigned) {
                stub2Room.get(e.getKey()).add(currAssigned);
                currAssigned++;
            }

        }
        stub2Room.forEach((k,v) -> {
            RBoxProto.GameRooms.Builder gr = RBoxProto.GameRooms.newBuilder();
            for(int i = 0; i <v.size(); i++)
                gr.setAssignedRooms(i, v.get(i));
            k.assignGameRooms(gr.build());
        });
        return stub2Room;
    }

    public static ConnectionManager buildFromMongo(MongoCollection superPeerCol, MongoCollection clientCol) {
        ConnectionManager connMan = new ConnectionManager(superPeerCol, clientCol);
        // TODO: Create connection to all of the superPeers and clients.
        return connMan;
    }

    // sends connect msgs to superpeers so they are completely interconnected
    public void makeSuperpeersInterconnected() {

        // Connect using RboxService Stub
        for (RegistrarBlockingStub stub1 : this.superPeers.keySet()) {
            for (RegistrarBlockingStub stub2 : this.superPeers.keySet()) {
                if (stub1 != stub2) {
                    SuperPeerInfo spInfo1 = this.superPeers.get(stub1);
                    long millis = System.currentTimeMillis();
                    Timestamp timestamp = Timestamp.newBuilder().setSeconds(millis / 1000)
                                              .setNanos((int) ((millis % 1000) * 1000000)).build();

                    RBoxProto.BasicInfo basicInfo = RBoxProto.BasicInfo.newBuilder()
                               .setSenderUUID(spInfo1.getSuperPeerId())
                               .setTime(timestamp)
                               .build();

                    RBoxProto.ConnectMessage request =
                        RBoxProto.ConnectMessage.newBuilder()
                            .setConnectionIP(spInfo1.getHostname())
                            .setSender(basicInfo)
                            .build();

                    stub2.connect(request);
                }

            }
        }

    }

}
