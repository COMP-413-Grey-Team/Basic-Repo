package edu.rice.rbox.FaultTolerance;

import com.google.protobuf.Empty;
import com.mongodb.Mongo;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.rice.rbox.Game.Server.ObjectStorageKeys;
import edu.rice.rbox.Location.Mongo.MongoManager;
import edu.rice.rbox.Networking.NetworkImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import network.HealthGrpc;
import network.RBoxProto;
import network.RBoxServiceGrpc;
import network.RegistrarGrpc;
import org.bson.Document;
import edu.rice.rbox.FaultTolerance.Registrar;

public class Registrar {

    private ConnectionManager connManager;
    private String ipAddress = "";
    private MongoDatabase db;

    private RegistrarGrpc.RegistrarImplBase superPeerServiceImpl = new RegistrarGrpc.RegistrarImplBase() {

        @Override
        public void alert(RBoxProto.NewRegistrarMessage request, StreamObserver<Empty> responseObserver) {
            // TODO: Nothing, because the registrar knows if its the lead or not already
            com.google.protobuf.Empty empty = com.google.protobuf.Empty.newBuilder().build();
            responseObserver.onNext(empty);
            responseObserver.onCompleted();
        }

        @Override
        public void promote(RBoxProto.PromoteSecondaryMessage request, StreamObserver<Empty> responseObserver) {
            // TODO: idk wtf this is supposed to do
            com.google.protobuf.Empty empty = com.google.protobuf.Empty.newBuilder().build();
            responseObserver.onNext(empty);
            responseObserver.onCompleted();
        }

        @Override
        public void connect(RBoxProto.ConnectMessage request, StreamObserver<Empty> responseObserver) {
            // TODO: this is the UUID

            String senderUUID = request.getSender().getSenderUUID();
            String senderHostnameInfo = request.getConnectionIP();

            System.out.println("Super with IP/port " + senderHostnameInfo + " called connectTo RPC");

            RegistrarGrpc.RegistrarBlockingStub spStub = Registrar.this.connManager
                                                            .addSuperPeer(senderHostnameInfo, UUID.fromString(senderUUID));
            spStub.connect(RBoxProto.ConnectMessage.newBuilder().setConnectionIP(ipAddress).build());
            com.google.protobuf.Empty empty = com.google.protobuf.Empty.newBuilder().build();
            responseObserver.onNext(empty);
            responseObserver.onCompleted();
        }

        @Override
        public void querySecondary(RBoxProto.querySecondaryMessage request,
                                   StreamObserver<RBoxProto.secondaryTimestampsMessage> responseObserver) {
            // TODO: ditto to not knowing what this one does either

        }

        @Override
        public void assignGameRooms(network.RBoxProto.GameRooms request,
                                    io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {

            // no-op
        }
    };


    private HealthGrpc.HealthImplBase healthServiceImpl = new HealthGrpc.HealthImplBase() {

        @Override
        public void check(RBoxProto.HealthCheckRequest request,
                          StreamObserver<RBoxProto.HealthCheckResponse> responseObserver) {
            // TODO: do this
        }

        @Override
        public void watch(RBoxProto.HealthCheckRequest request,
                          StreamObserver<RBoxProto.HealthCheckResponse> responseObserver) {
            // TODO: do this
        }
    };




    public Registrar() {
        MongoManager mongoMan = new MongoManager();
        mongoMan.connect();
        MongoClient client = mongoMan.getMongoClient();
        client.getDatabase(MongoManager.DB_NAME).drop();
        this.db = client.getDatabase(MongoManager.DB_NAME);
        db.createCollection(MongoManager.CLIENT_COLLECTION);
        db.createCollection(MongoManager.SUPERPEER_COLLECTION);
        db.createCollection(MongoManager.COLLECTION_NAME);
        this.connManager = new ConnectionManager(db.getCollection(MongoManager.SUPERPEER_COLLECTION),
            db.getCollection(MongoManager.CLIENT_COLLECTION));
    }

    public void init() throws Exception {
        String ip = "";
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            ip = socket.getLocalAddress().getHostAddress();
            ipAddress = ip;
        }

        System.out.println("Server Running on address: " + ip);

        // Initialize Global object
        Document globalObj = new Document("_id", ObjectStorageKeys.Global.GLOBAL_OBJ.toString());
        db.getCollection(MongoManager.COLLECTION_NAME).insertOne(globalObj);

        // Create a new server to listen on port 8080 - within its own thread
        Thread registrarServerThread = new Thread(() -> {
            Server server = ServerBuilder.forPort(8080)
                                // this is for registrar/player client interactions
                                .addService(this.connManager.getGameServerRegistrarImpl())
                                // this is for registrar/superpeer interactions
                                .addService(this.superPeerServiceImpl)
                                // TODO: this is for the registrar faults/elections - looking @ u Nikhaz
                                .addService(this.healthServiceImpl)
                                // TODO: this is for the health service @ Nikhaz
                                .build();


            try {
                // Start the server
                server.start();

                // Server threads are running in the background.
                System.out.println("Server started");

                // Don't exit the main thread. Wait until server is terminated.
                server.awaitTermination();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }


        });

        registrarServerThread.start();
//        registrarServerThread.join();

    }

    public ConnectionManager getConnManager() {
        return connManager;
    }


    public static void main( String[] args ) throws Exception {
        Registrar reg = new Registrar();
        reg.init();


    }
}
