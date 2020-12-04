package edu.rice.rbox.FaultTolerance;

import com.google.protobuf.Empty;
import com.mongodb.Mongo;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.rice.rbox.Location.Mongo.MongoManager;
import edu.rice.rbox.Networking.NetworkImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import io.grpc.stub.StreamObserver;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.UUID;

import network.HealthGrpc;
import network.RBoxProto;
import network.RBoxServiceGrpc;
import network.RegistrarGrpc;

public class Registrar {

    private ConnectionManager connManager;
    private String ipAddress = "";

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
        MongoDatabase db = client.getDatabase(MongoManager.DB_NAME);
        db.createCollection(MongoManager.CLIENT_COLLECTION);
        db.createCollection(MongoManager.SUPERPEER_COLLECTION);
        db.createCollection(MongoManager.COLLECTION_NAME);
        this.connManager = new ConnectionManager(db.getCollection(MongoManager.SUPERPEER_COLLECTION),
            db.getCollection(MongoManager.CLIENT_COLLECTION));
    }

    public void init() throws  Exception {
        String ip = "";
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            ip = socket.getLocalAddress().getHostAddress();
            ipAddress = ip;
        }

        System.out.println("Server Running on address: " + ip);


        // Create a new server to listen on port 8080

        Server server = ServerBuilder.forPort(8080)
                            // this is for registrar/player client interactions
                            .addService(this.connManager.getGameServerRegistrarImpl())
                            // this is for registrar/superpeer interactions
                            .addService(this.superPeerServiceImpl)
                            // TODO: this is for the registrar faults/elections - looking @ u Nikhaz
                            .addService(this.healthServiceImpl)
                            // TODO: this is for the health service @ Nikhaz
                            .build();


        // Start the server
        server.start();

        // Server threads are running in the background.
        System.out.println("Server started");

        // Don't exit the main thread. Wait until server is terminated.
        server.awaitTermination();
    }


    public static void main( String[] args ) throws Exception {
        Registrar reg = new Registrar();
        reg.init();


    }
}
