package edu.rice.rbox.FaultTolerance;

import com.mongodb.MongoClient;
import network.GameServiceGrpc.GameServiceBlockingStub;
import network.RegistrarGrpc.RegistrarBlockingStub;

import java.util.ArrayList;
import java.util.List;

public class ConnectionManager {

    private List<GameServiceBlockingStub> clients;
    private List<RegistrarBlockingStub> superPeers;
    private MongoClient mongoClient;
    private List<Integer> clientNumbers;


    public ConnectionManager(MongoClient mongoClient) {
        clients = new ArrayList<>();
        superPeers = new ArrayList<>();
        this.mongoClient = mongoClient;
    }

    /**
     * Create a stub to the Superpeer and then add the stub to the list.
     *
     * @param hostname host of the superpeer
     * @param port port of the superpeer
     * @return superpeer stub
     */
    public RegistrarBlockingStub addSuperPeer(String hostname, String port) {

    }

    /**
     * Create a stub to the Superpeer and add the stub to the list
     *
     * @param hostname host of the client
     * @param port port of the client
     * @return client stub
     */
    public GameServiceBlockingStub addClient(String hostname, String port) {

    }

    public RegistrarBlockingStub assignClient() {

    }

    private void addSuperPeerToMongo() {

    }

    private void addClientToMongo() {

    }
}
