package edu.rice.rbox.FaultTolerance;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import network.GameServiceGrpc;
import network.GameServiceGrpc.GameServiceBlockingStub;
import network.RegistrarGrpc;
import network.RegistrarGrpc.RegistrarBlockingStub;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class ConnectionManager {

    private List<GameServiceBlockingStub> clients;
    private List<RegistrarBlockingStub> superPeers;
    private MongoCollection superPeerCol;
    private MongoCollection clientCol;
    private List<Integer> clientNumbers;


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
    public RegistrarBlockingStub assignClient() {
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
