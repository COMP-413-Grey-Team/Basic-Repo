package edu.rice.rbox.FaultTolerance;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.Map;
import java.util.UUID;
import network.GameNetworkProto;
import network.GameServiceGrpc;
import network.GameServiceGrpc.GameServiceBlockingStub;
import network.RegistrarGrpc;
import network.RegistrarGrpc.RegistrarBlockingStub;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class ConnectionManager {




    // player clients will not have gRPC servers, so we cannot have stubs to them
    private List<GameServiceBlockingStub> clients;

    private Map<RegistrarBlockingStub, List<UUID>> superPeer2gameClient;
    private Map<RegistrarBlockingStub, GameNetworkProto.SuperPeerInfo> superPeers;
    private List<GameNetworkProto.SuperPeerInfo> superPeerInfos;
    private MongoCollection superPeerCol;
    private MongoCollection clientCol;
    private List<Integer> clientNumbers;

    private Integer currAssignedSuperPeer = 0;



    private GameServiceGrpc.GameServiceImplBase gameServerRegistrarImpl = new GameServiceGrpc.GameServiceImplBase() {
        @Override
        public void publishUpdate(network.GameNetworkProto.UpdateFromClient request,
                                  io.grpc.stub.StreamObserver<network.GameNetworkProto.UpdateFromServer> responseObserver) {

            System.out.println("Publish Update incorrectly called on registrar!!!");
        }

        @Override
        public void getAssignedSuperPeer(network.GameNetworkProto.Empty request,
                                         io.grpc.stub.StreamObserver<network.GameNetworkProto.SuperPeerInfo> responseObserver) {
            System.out.println("Get Assign incorrectly called on registrar!");

            // make sure not to go outta bounds
            if (currAssignedSuperPeer > superPeerInfos.size() - 1) {
                ConnectionManager.this.currAssignedSuperPeer = 0;
            }

            // send over assigned superpeer info


            ConnectionManager.this.currAssignedSuperPeer++;
        }

        @Override
        public void initPlayer(network.GameNetworkProto.InitialPlayerState request,
                               io.grpc.stub.StreamObserver<network.GameNetworkProto.UpdateFromServer> responseObserver) {
            System.out.println("Init Player incorrectly called on registrar!!!");
        }

        @Override
        public void removeMe(network.GameNetworkProto.PlayerID request,
                             io.grpc.stub.StreamObserver<network.GameNetworkProto.Empty> responseObserver) {
            System.out.println("Remove me called on registrar");
        }
    };
    // define the gameservice server in connection manager


    /**
     * Manager for the connections to the registrar
     * @param spCollection MongoDB collection for the super-peers
     * @param clientCollection MongoDB collection for the clients
     */
    public ConnectionManager(MongoCollection spCollection, MongoCollection clientCollection) {
        clients = new ArrayList<>();
        superPeers = new ArrayList<>();
        this.superPeerCol = spCollection;
        this.clientCol = clientCollection;
        this.clientNumbers = new ArrayList<>();
    }

    /**
     * Create a stub to the Superpeer and then add the stub to the list.
     *
     * @param hostname host of the superpeer
     * @param port port of the superpeer
     * @return superpeer stub
     */
    public RegistrarBlockingStub addSuperPeer(String hostname, String port) {
        ManagedChannel channel = ManagedChannelBuilder.forTarget(hostname + ":"+ port)
                                     .usePlaintext(true)
                                     .build();
        RegistrarBlockingStub sp = RegistrarGrpc.newBlockingStub(channel);
        superPeers.add(sp);
        clientNumbers.add(0);

        Document doc = new Document("hostname", hostname).append("port", port);
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
    public GameServiceBlockingStub addClient(String hostname, String port) {
        ManagedChannel channel = ManagedChannelBuilder.forTarget(hostname + ":"+ port)
                                     .usePlaintext(true)
                                     .build();
        GameServiceBlockingStub client = GameServiceGrpc.newBlockingStub(channel);
        clients.add(client);
        Document doc = new Document("hostname", hostname).append("port", port);
        clientCol.insertOne(doc);

        return client;
    }

    /**
     * Assigns a client to the superpeer with the least amount of clients assigned
     *
     * @return superPeer that is being assigned to
     */
    public GameNetworkProto.SuperPeerInfo assignClient() {
        int minIdx = 0;
        int minVal = -1;
        for (int i =0; i < clientNumbers.size(); i++) {
            if (minVal == -1 || clientNumbers.get(i) < minVal){
                minIdx = i;
                minVal = clientNumbers.get(i);
            }
            if (minVal == 0){
                break;
            }
        }
        clientNumbers.set(minIdx, minVal +1);
        return superPeers.get(minIdx);
    }



}
