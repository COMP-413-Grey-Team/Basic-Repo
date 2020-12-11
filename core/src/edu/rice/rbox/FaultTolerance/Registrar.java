package edu.rice.rbox.FaultTolerance;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import edu.rice.rbox.Game.Server.ObjectStorageKeys;
import edu.rice.rbox.Location.Mongo.MongoManager;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;


import network.InternalRegistrarFaultToleranceGrpc;
import network.RBoxProto;
import network.RBoxServiceGrpc;
import org.bson.Document;
import edu.rice.rbox.FaultTolerance.Registrar;
import network.SuperpeerFaultToleranceGrpc.*;

public class Registrar {

    private ConnectionManager connManager;
    private String ipAddress = "";
    private MongoDatabase db;
    private UUID uuid = UUID.randomUUID();

    private Map<InternalRegistrarFaultToleranceGrpc.InternalRegistrarFaultToleranceBlockingStub, Timestamp> mostRecentClusterHeartBeats = new HashMap<>();
    private Map<SuperpeerFaultToleranceBlockingStub, Timestamp> mostRecentSuperpeerHeartBeats = new HashMap<>();

    protected ClusterManager clusterManager;

    private double getNumRegistrarNodes () {
        return (double) clusterManager.clusterMemberStubs.size();
    }


    public UUID getUUID() {
        return this.uuid;
    }

    private RBoxProto.BasicInfo getInfo() {
        return RBoxProto.BasicInfo.newBuilder().setSenderUUID(getUUID().toString()).setTime(getTimestamp()).build();
    }

    protected Timestamp getTimestamp() {
        long millis = System.currentTimeMillis();
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(millis / 1000)
                .setNanos((int) ((millis % 1000) * 1000000)).build();
        return timestamp;
    }

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

            System.out.println("Super with IP/port " + senderHostnameInfo + " called connectTo RPC");
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
        this.db = client.getDatabase(MongoManager.DB_NAME);
        db.createCollection(MongoManager.CLIENT_COLLECTION);
        db.createCollection(MongoManager.SUPERPEER_COLLECTION);
        db.createCollection(MongoManager.COLLECTION_NAME);
        this.connManager = new ConnectionManager(db.getCollection(MongoManager.SUPERPEER_COLLECTION),
            db.getCollection(MongoManager.CLIENT_COLLECTION));


    }

    public void init(String leaderIP) throws  Exception {
        String ip = "";
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            ip = socket.getLocalAddress().getHostAddress();
            ipAddress = ip;
        }

        this.ipAddress = ip + ":8080";
        System.out.println("Server Running on address: " + ip);

        // instantiate the clusterManager
        this.clusterManager = new ClusterManager(uuid, this.ipAddress, new Runnable() {
            // this runnable notifies superpeers that this registrar is the lead
            @Override
            public void run() {
                for (SuperpeerFaultToleranceBlockingStub spStub : connManager.getSuperpeers()) {
                    // TODO:    MUST ACTUALLY MAKE THE MESSAGE BY SETTING FIELDS, WHICH I AINT BOUT

                    spStub.alertSuperPeers(RBoxProto.NewRegistrarMessage.newBuilder().setSender(getInfo()).build());
                }
            }
        });

        // means this registrar is a scrub, and only a cluster member
        if (leaderIP != null) {
            clusterManager.initNonLeader(leaderIP);
        }

        // Initialize Global object
        Document globalObj = new Document("_id", ObjectStorageKeys.Global.GLOBAL_OBJ.toString())
                                 .append(ObjectStorageKeys.TYPE, ObjectStorageKeys.Global.TYPE_NAME);
        db.getCollection(MongoManager.COLLECTION_NAME).insertOne(globalObj);

        // Create a new server to listen on port 8080 - within its own thread
        Server server = ServerBuilder.forPort(8080)
                            // this is for registrar/player client interactions
                            .addService(this.connManager.getGameServerRegistrarImpl())
                            // this is for registrar/superpeer interactions
                            .addService(this.superPeerServiceImpl)
                            // this is for the registrar cluster interactions
                            .addService(this.clusterManager.getInternalServiceImpl())
                            .build();
        Thread registrarServerThread = new Thread(() -> {
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

        // create a new thread
        Runnable target = new Runnable() {
            @Override
            public void run() {
                while (!clusterManager.leader) {
                    if (getTimestamp().getSeconds() > clusterManager.getMostRecentHeartbeat().getSeconds() ||
                            (getTimestamp().getSeconds() == clusterManager.getMostRecentHeartbeat().getSeconds() &&
                                    getTimestamp().getNanos() - clusterManager.getMostRecentHeartbeat().getNanos() > 500)) {
                        if (clusterManager.clusterMemberStubs.size() > 1) {
                            UUID backup = (UUID) clusterManager.clusterMemberUUIDs.keySet().toArray()[0];
                            if (backup == clusterManager.leaderUUID) {
                                backup = (UUID) clusterManager.clusterMemberUUIDs.keySet().toArray()[1];
                            }
                            clusterManager.clusterMemberUUIDs.get(backup).downedLeader(RBoxProto.LeaderDown.newBuilder().setSender(getInfo()).build());
                        }
                    }
                }
                while (clusterManager.leader) {
                    for (InternalRegistrarFaultToleranceGrpc.InternalRegistrarFaultToleranceBlockingStub stub : clusterManager.clusterMemberStubs.keySet()) {
                        UUID stubUUID = clusterManager.clusterMemberStubs.get(stub);
                        RBoxProto.BasicInfo info = getInfo();
                        RBoxProto.HeartBeatRequest req = RBoxProto.HeartBeatRequest.newBuilder().setSender(info).build();
                        boolean success = true;
                        try {
                            stub.heartBeatClusterMember(req);
                            } catch (Exception ex) {
                            success = false;
                        }
                        if (success) {
                            mostRecentClusterHeartBeats.putIfAbsent(stub, info.getTime());
                            mostRecentClusterHeartBeats.put(stub, info.getTime());
                        } else {
                            if(info.getTime().getNanos() - mostRecentClusterHeartBeats.get(stub).getNanos() > 500) {
                                int countSuccessfulChecks = 0;
                                int countUnsuccessfulChecks = 0;
                                for (InternalRegistrarFaultToleranceGrpc.InternalRegistrarFaultToleranceBlockingStub checkStub : clusterManager.clusterMemberStubs.keySet()) {
                                    RBoxProto.CheckConnection check = RBoxProto.CheckConnection.newBuilder().setSender(getInfo()).setCheckUUID(stubUUID.toString()).build();
                                    if (checkStub.checkConnectionToClusterMember(check).getResult()) {
                                        countSuccessfulChecks++;
                                    } else {
                                        countUnsuccessfulChecks++;
                                    }
                                    if (countSuccessfulChecks > (2.0 / 3.0) * getNumRegistrarNodes()) {
                                        break;
                                    } else if (countUnsuccessfulChecks > (2.0 / 3.0) * getNumRegistrarNodes()) {
                                        clusterManager.clusterMemberStubs.remove(stub);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    for (SuperpeerFaultToleranceBlockingStub stub : connManager.superPeers.keySet()) {
                        UUID stubUUID = UUID.fromString(connManager.superPeers.get(stub).getSuperPeerId());
                        RBoxProto.BasicInfo info = getInfo();
                        RBoxProto.HeartBeatRequest req = RBoxProto.HeartBeatRequest.newBuilder().setSender(info).build();
                        boolean success = true;
                        try {
                            stub.heartBeatSuperpeer(req);
                        } catch (Exception ex) {
                            success = false;
                            System.out.println("Superpeer disconnected");
                        }
                        if (success) {
                            mostRecentSuperpeerHeartBeats.putIfAbsent(stub, info.getTime());
                            mostRecentSuperpeerHeartBeats.put(stub, info.getTime());
                        } else {
                            if(info.getTime().getNanos() - mostRecentSuperpeerHeartBeats.get(stub).getNanos() > 500) {
                                //TODO use mongo to get downed objects
                                List<String> secondaryUUIDs = new ArrayList<>();
                                Map<String, Timestamp> secondaryBestTimestamps = new HashMap<>();
                                Map<String, SuperpeerFaultToleranceBlockingStub> secondaryBestSuperpeers = new HashMap<>();
                                connManager.superPeers.remove(stub);
                                connManager.superPeer2gameClient.remove(stub);
                                for (SuperpeerFaultToleranceBlockingStub superpeer : connManager.superPeers.keySet()) {
                                    RBoxProto.SecondaryTimestampsMessage timestamps;
                                    timestamps = superpeer.querySecondary(RBoxProto.QuerySecondaryMessage.newBuilder().addAllPrimaryUUIDs(secondaryUUIDs).build());
                                    int index = 0;
                                    for (String uuid : timestamps.getPrimaryUUIDsList()) {
                                        secondaryBestTimestamps.putIfAbsent(uuid, timestamps.getSecondaryTimestamps(index));
                                        secondaryBestSuperpeers.putIfAbsent(uuid, superpeer);
                                        //TODO check this comparison
                                        if (Integer.parseInt(secondaryBestTimestamps.get(uuid).toString()) > Integer.parseInt(timestamps.getSecondaryTimestamps(index).toString())) {
                                            secondaryBestTimestamps.put(uuid, timestamps.getSecondaryTimestamps(index));
                                            secondaryBestSuperpeers.put(uuid, superpeer);
                                        }
                                        index++;
                                    }
                                }
                                //TODO double check that everything is being updated
                                for (String uuid : secondaryBestSuperpeers.keySet()) {
                                    RBoxProto.PromoteSecondaryMessage promote = RBoxProto.PromoteSecondaryMessage.newBuilder().setSender(getInfo()).addPromotedUUIDs(uuid).build();
                                    secondaryBestSuperpeers.get(uuid).promote(promote);
                                }
                            }
                        }
                    }
                }
            }
        };
        Thread heartBeatChecker = new Thread(target);
        heartBeatChecker.start();

        // Don't exit the main thread. Wait until server is terminated.
        server.awaitTermination();
    }

    public ConnectionManager getConnManager() {
      return connManager;
    }

    public static void main( String[] args ) throws Exception {
        Registrar reg = new Registrar();


        // no command line args means that this instance is the leader, otherwise issa follower
        if (args.length == 0) {
            reg.init(null);
            reg.clusterManager.leader = true;
        } else {
            String leadIP = args[0];
            reg.init(leadIP);
        }


    }
}
