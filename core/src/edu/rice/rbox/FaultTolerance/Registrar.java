package edu.rice.rbox.FaultTolerance;

import com.google.protobuf.Empty;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import edu.rice.rbox.Location.Mongo.MongoManager;
import edu.rice.rbox.Location.registrar.ClusterManager;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import io.grpc.stub.StreamObserver;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.UUID;


import network.RBoxProto;
import network.RBoxServiceGrpc;
import network.SuperpeerFaultToleranceGrpc.*;

public class Registrar {

    private ConnectionManager connManager;
    private String ipAddress = "";

    private ClusterManager clusterManager;

    private SuperpeerFaultToleranceImplBase superPeerServiceImpl = new SuperpeerFaultToleranceImplBase() {

        @Override
        public void promote(RBoxProto.PromoteSecondaryMessage request, StreamObserver<Empty> responseObserver) {
            // TODO: idk wtf this is supposed to do
            com.google.protobuf.Empty empty = com.google.protobuf.Empty.newBuilder().build();
            responseObserver.onNext(empty);
            responseObserver.onCompleted();
        }

        @Override
        public void connectToSuperpeer(RBoxProto.ConnectMessage request, StreamObserver<Empty> responseObserver) {

            String senderUUID = request.getSender().getSenderUUID();
            String senderHostnameInfo = request.getConnectionIP();

            SuperpeerFaultToleranceBlockingStub spStub = Registrar.this.connManager
                                                            .addSuperPeer(senderHostnameInfo, UUID.fromString(senderUUID));


            com.google.protobuf.Empty empty = com.google.protobuf.Empty.newBuilder().build();
            responseObserver.onNext(empty);
            responseObserver.onCompleted();
        }

        @Override
        public void querySecondary(RBoxProto.QuerySecondaryMessage request,
                                   StreamObserver<RBoxProto.SecondaryTimestampsMessage> responseObserver) {

            // noop
            responseObserver.onNext(RBoxProto.SecondaryTimestampsMessage.getDefaultInstance());
            responseObserver.onCompleted();
        }

        @Override
        public void assignGameRooms(RBoxProto.GameRooms request, StreamObserver<Empty> responseObserver) {

            // TODO: the super peer will never send the registrar this, right - hence noop?
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        }

        @Override
        public void heartBeatSuperpeer(RBoxProto.HeartBeatRequest request,
                                       StreamObserver<RBoxProto.HeartBeatResponse> responseObserver) {
            // noop
            responseObserver.onNext(RBoxProto.HeartBeatResponse.getDefaultInstance());
            responseObserver.onCompleted();
        }

        @Override
        public void alertSuperPeers(network.RBoxProto.NewRegistrarMessage request,
                                    io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
            // noop
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
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

        this.clusterManager = new ClusterManager(null, new Runnable() {
            // this runnable notifies superpeers that this registrar is the lead
            @Override
            public void run() {
                for (SuperpeerFaultToleranceBlockingStub spStub : connManager.getSuperpeers()) {
                    // TODO:    MUST ACTUALLY MAKE THE MESSAGE BY SETTING FIELDS, WHICH I AINT BOUT

                    spStub.alertSuperPeers(RBoxProto.NewRegistrarMessage.newBuilder().build());
                }
            }
        });
    }

    public void init() throws  Exception {
        String ip = "";
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            ip = socket.getLocalAddress().getHostAddress();
            ipAddress = ip;
        }

        System.out.println("Server Running on address: " + ip);

        this.ipAddress = ip + ":8080";


        // Create a new server to listen on port 8080

        Server server = ServerBuilder.forPort(8080)
                            // this is for registrar/player client interactions
                            .addService(this.connManager.getGameServerRegistrarImpl())
                            // this is for registrar/superpeer interactions
                            .addService(this.superPeerServiceImpl)
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
