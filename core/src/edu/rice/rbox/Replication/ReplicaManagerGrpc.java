package edu.rice.rbox.Replication;

import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import edu.rice.rbox.Common.Change.RemoteChange;
import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Common.ServerUUID;
import edu.rice.rbox.ObjStorage.ChangeReceiver;
import io.grpc.*;
import io.grpc.stub.StreamObserver;
import network.RBoxProto;
import network.RBoxServiceGrpc;
import org.apache.commons.lang3.SerializationUtils;

import network.*;


import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ReplicaManagerGrpc implements ObjectLocationReplicationInterface {


    private static final Logger logger = Logger.getLogger(ReplicaManagerGrpc.class.getName());

    private ChangeReceiver changeReceiver;
    private ServerUUID serverUUID;
    private final int port;
    private final Server server;

    private HashMap<GameObjectUUID, List<ServerUUID>> subscribers = new HashMap<>();      // Primary => Replica
    private HashMap<GameObjectUUID, ServerUUID> publishers = new HashMap<>();             // Replica => Primary
    private HashMap<GameObjectUUID, Integer> timeout = new HashMap<>();                   // Replica => timeout
    private HashMap<GameObjectUUID, Timestamp> timestamp = new HashMap<>();                       // Replica => timestamp
    private final int initial_value = 50;

    private List<Integer> assignedRooms;    // rooms assigned to this superpeer by the registrar

    private HashMap<ServerUUID, RBoxServiceGrpc.RBoxServiceBlockingStub> blockingStubs = new HashMap<>();
    private HashMap<ServerUUID, RBoxServiceGrpc.RBoxServiceStub> stubs = new HashMap<>();
    private SuperpeerFaultToleranceGrpc.SuperpeerFaultToleranceBlockingStub registrarBlockingStub;

    private static StreamObserver<Empty> emptyResponseObserver = new StreamObserver<>() {
        @Override
        public void onNext(Empty empty) { }

        @Override
        public void onError(Throwable throwable) { }

        @Override
        public void onCompleted() { }
    };
    private static Empty emptyResponse = Empty.newBuilder().build();

    private RBoxServiceGrpc.RBoxServiceImplBase rboxServiceImpl = new RBoxServiceGrpc.RBoxServiceImplBase() {
        @Override
        public void handleSubscribe(RBoxProto.SubscribeRequest request,
                                    StreamObserver<RBoxProto.UpdateMessage> responseObserver) {
            logger.log(Level.INFO, "Handling subscribe request...");

            // Get replica from storage
            GameObjectUUID primaryObjectUUID = getGameObjectUUIDFromMessage(request.getMsg());
            ServerUUID senderServerUUID = getServerUUIDFromMessage(request.getMsg());
            RemoteChange replica = changeReceiver.getReplica(primaryObjectUUID);

            // Add to subscribers
            List<ServerUUID> maybeSubscribers = subscribers.get(primaryObjectUUID);

            if (maybeSubscribers != null) {
                maybeSubscribers.add(senderServerUUID);
            } else {
                subscribers.put(primaryObjectUUID, List.of(senderServerUUID));
            }

            // Send the response
            RBoxProto.ReplicationMessage message = generateReplicationMessage(primaryObjectUUID);
            ByteString replicaByteString = getByteStringFromRemoteChange(replica);
            RBoxProto.UpdateMessage response = RBoxProto.UpdateMessage.newBuilder()
                                                   .setMsg(message)
                                                   .setRemoteChange(replicaByteString)
                                                   .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void handleUnsubscribe(RBoxProto.UnsubscribeRequest request, StreamObserver<Empty> responseObserver) {
            logger.info("Handling unsubscribe request...");

            GameObjectUUID primaryObjectUUID = getGameObjectUUIDFromMessage(request.getMsg());
            ServerUUID senderUUID = getServerUUIDFromMessage(request.getMsg());

            // Remove from subscribers
            List<ServerUUID> updatedReplicas = subscribers.get(primaryObjectUUID);
            updatedReplicas.remove(senderUUID);
            subscribers.put(primaryObjectUUID, updatedReplicas);

            // No response expected, send empty response
            responseObserver.onNext(emptyResponse);
            responseObserver.onCompleted();
        }

        @Override
        public void handleUpdate(RBoxProto.UpdateMessage request, StreamObserver<Empty> responseObserver) {
            logger.log(Level.INFO, "Handling update...");

            // Pass change to storage
            GameObjectUUID gameObjectUUID = getGameObjectUUIDFromMessage(request.getMsg());
            Timestamp ts = request.getMsg().getSenderInfo().getTime();

            // If it's updating a replica, update the latest timestamp
            if (timestamp.containsKey(gameObjectUUID) && Timestamps.compare(ts, timestamp.get(gameObjectUUID)) > 0) {
                timestamp.put(gameObjectUUID, ts);
            }

            RemoteChange change = getRemoteChangeFromUpdateMessage(request);
            changeReceiver.receiveChange(change);
            responseObserver.onNext(emptyResponse);
            responseObserver.onCompleted();
        }
    };

    private SuperpeerFaultToleranceGrpc.SuperpeerFaultToleranceImplBase registrarServiceImpl = new SuperpeerFaultToleranceGrpc.SuperpeerFaultToleranceImplBase() {
        @Override
        public void alertSuperPeers(RBoxProto.NewRegistrarMessage request, StreamObserver<Empty> responseObserver) {
            // Save Registrar blocing stub
            String registrarIP = request.getNewRegistrarIP();
            ManagedChannel channel = ManagedChannelBuilder.forTarget(registrarIP)
                                         .usePlaintext(true)
                                         .build();
            registrarBlockingStub = SuperpeerFaultToleranceGrpc.newBlockingStub(channel);
            responseObserver.onNext(emptyResponse);
            responseObserver.onCompleted();
        }

        @Override
        public void promote(RBoxProto.PromoteSecondaryMessage request, StreamObserver<Empty> responseObserver) {
            request.getPromotedUUIDsList().forEach(uuidStr -> {
                GameObjectUUID gameObjectUUID = new GameObjectUUID(UUID.fromString(uuidStr));
                changeReceiver.promoteSecondary(gameObjectUUID);
                // No longer treated as a replica
                publishers.remove(gameObjectUUID);
                timestamp.remove(gameObjectUUID);
                timeout.remove(gameObjectUUID);
            });
            responseObserver.onNext(emptyResponse);
            responseObserver.onCompleted();
        }

        @Override
        public void connectToSuperpeer(RBoxProto.ConnectMessage request, StreamObserver<Empty> responseObserver) {
            ServerUUID superpeerUUID = new ServerUUID(UUID.fromString(request.getSender().getSenderUUID()));
            String superpeerIP = request.getConnectionIP();

            // Save stub and blocking stub
            ManagedChannel channel = ManagedChannelBuilder.forTarget(superpeerIP)
                                         .usePlaintext(true)
                                         .build();
            RBoxServiceGrpc.RBoxServiceBlockingStub blockingStub = RBoxServiceGrpc.newBlockingStub(channel);
            RBoxServiceGrpc.RBoxServiceStub stub = RBoxServiceGrpc.newStub(channel);
            blockingStubs.put(superpeerUUID, blockingStub);
            stubs.put(superpeerUUID, stub);
            logger.info("Connected to superpeer: " + request.getConnectionIP());
            responseObserver.onNext(emptyResponse);
            responseObserver.onCompleted();
        }

        @Override
        public void querySecondary(RBoxProto.QuerySecondaryMessage request, StreamObserver<RBoxProto.SecondaryTimestampsMessage> responseObserver) {
            RBoxProto.SecondaryTimestampsMessage.Builder msgBuilder = RBoxProto.SecondaryTimestampsMessage.newBuilder();

            request.getPrimaryUUIDsList().forEach(uuidStr -> {
                GameObjectUUID replicaObjectUUID = new GameObjectUUID(UUID.fromString(uuidStr));
                if (timestamp.containsKey(replicaObjectUUID)) {
                    Timestamp time = timestamp.get(replicaObjectUUID);
                    msgBuilder.addPrimaryUUIDs(uuidStr).addSecondaryTimestamps(time);
                }
            });

            RBoxProto.SecondaryTimestampsMessage msg = msgBuilder.build();
            responseObserver.onNext(msg);
            responseObserver.onCompleted();
        }

        @Override
        public void assignGameRooms(network.RBoxProto.GameRooms request,
                                    io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
            // Save the list of assigned rooms locally so we can initialize the rooms from the superpeer
            assignedRooms = request.getAssignedRoomsList();
//            sendRooms.accept(assignedRooms);
            responseObserver.onNext(emptyResponse);
            responseObserver.onCompleted();
        }

        @Override
        public void heartBeatSuperpeer(network.RBoxProto.HeartBeatRequest request,
                                       io.grpc.stub.StreamObserver<network.RBoxProto.HeartBeatResponse> responseObserver) {
            // noop response, just checks if the connection is still viable
            responseObserver.onNext(RBoxProto.HeartBeatResponse.getDefaultInstance());
            responseObserver.onCompleted();
        }
    };

    private Consumer<List<Integer>> sendRooms;

    /* Constructor */
    public ReplicaManagerGrpc(int port, ServerUUID serverUUID, ChangeReceiver changeReceiver, GameServiceGrpc.GameServiceImplBase gameService, Consumer<List<Integer>> sendRooms) {
        this.changeReceiver = changeReceiver;
        this.serverUUID = serverUUID;
        this.port = port;
        this.server = ServerBuilder.forPort(port)
                          .addService(rboxServiceImpl)
                          .addService(registrarServiceImpl)
                          .addService(gameService)
                          .build();
        this.sendRooms = sendRooms;
    }

    /* Start the server */
    public void start(String registrarIP) throws Exception {
        server.start();
        logger.info("Server started, listening on " + port);
        logger.info("This server UUID is " + serverUUID);
        logger.info("Attempt to connect to " + registrarIP);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Use stderr here since the logger may have been reset by its JVM shutdown hook.
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            try {
                ReplicaManagerGrpc.this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.err.println("*** server shut down");
        }));

        ManagedChannel channel = ManagedChannelBuilder.forTarget(registrarIP).usePlaintext(true).build();
        this.registrarBlockingStub = SuperpeerFaultToleranceGrpc.newBlockingStub(channel);

        String ip;
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 3000);
            ip = socket.getLocalAddress().getHostAddress() + ":" + port;
        }
        System.out.println("Superpeer Running on address: " + ip);


        // Send the registrar a Connect message
        long millis = System.currentTimeMillis();

        RBoxProto.ConnectMessage request =
            RBoxProto.ConnectMessage.newBuilder()
                .setConnectionIP(ip)
                .setSender(generateBasicInfo(millis))
                .build();

        registrarBlockingStub.connectToSuperpeer(request);
//        sendRooms.accept(assignedRooms);
    }

    /** Stop serving requests and shutdown resources. */
    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /* Helper functions */
    private RBoxServiceGrpc.RBoxServiceBlockingStub getBlockingStub(ServerUUID serverUUID) {
        return blockingStubs.get(serverUUID);
    }

    private RBoxServiceGrpc.RBoxServiceStub getStub(ServerUUID serverUUID) {
        return stubs.get(serverUUID);
    }

    private ByteString getByteStringFromRemoteChange(RemoteChange remoteChange) {
        return ByteString.copyFrom(SerializationUtils.serialize(remoteChange));
    }

    private RemoteChange getRemoteChangeFromUpdateMessage(RBoxProto.UpdateMessage msg) {
        return SerializationUtils.deserialize(msg.getRemoteChange().toByteArray());
    }

    private GameObjectUUID getGameObjectUUIDFromMessage(RBoxProto.ReplicationMessage msg) {
        return new GameObjectUUID(UUID.fromString(msg.getTargetObjectUUID()));
    }

    private ServerUUID getServerUUIDFromMessage(RBoxProto.ReplicationMessage msg) {
        return new ServerUUID(UUID.fromString(msg.getSenderInfo().getSenderUUID()));
    }

    private RBoxProto.ReplicationMessage generateReplicationMessage(GameObjectUUID gameObjectUUID) {
        long millis = System.currentTimeMillis();
        return generateReplicationMessage(gameObjectUUID, millis);
    }

    private RBoxProto.BasicInfo generateBasicInfo(long millis) {
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(millis / 1000)
                                  .setNanos((int) ((millis % 1000) * 1000000)).build();
        return RBoxProto.BasicInfo.newBuilder()
                                             .setSenderUUID(this.serverUUID.toString())
                                             .setTime(timestamp)
                                             .build();
    }

    private RBoxProto.ReplicationMessage generateReplicationMessage(GameObjectUUID gameObjectUUID, long millis) {
        return RBoxProto.ReplicationMessage.newBuilder()
                   .setSenderInfo(generateBasicInfo(millis))
                   .setTargetObjectUUID(gameObjectUUID.toString())
                   .build();
    }

    /* Replica Manager functions */
    private void subscribe(GameObjectUUID primaryObjectUUID, ServerUUID serverUUID) {
        logger.log(Level.INFO, "Sending subscribe request...");

        // Construct Subscribe Request
        RBoxProto.ReplicationMessage msg = generateReplicationMessage(primaryObjectUUID);
        RBoxProto.SubscribeRequest request = RBoxProto.SubscribeRequest.newBuilder().setMsg(msg).build();
        RBoxProto.UpdateMessage response;

        try {
            response = getBlockingStub(serverUUID).handleSubscribe(request);
            RemoteChange remoteChange = getRemoteChangeFromUpdateMessage(response);
            GameObjectUUID replicaObjectUUID = remoteChange.getTarget();

            // Add into publishers
            publishers.put(replicaObjectUUID, serverUUID);
            timestamp.put(replicaObjectUUID, response.getMsg().getSenderInfo().getTime());
            timeout.put(replicaObjectUUID, initial_value);

            // Send remote change to storage
            changeReceiver.receiveChange(remoteChange);

        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "failure when sending subscribe request");
        }

    }


    private void unsubscribe(GameObjectUUID replicaObjectUUID) {
        logger.info("Sending unsubscribe request...");

        // Construct Unsubscribe Request
        long millis = System.currentTimeMillis();
        RBoxProto.ReplicationMessage msg = generateReplicationMessage(replicaObjectUUID, millis);
        RBoxProto.UnsubscribeRequest request = RBoxProto.UnsubscribeRequest.newBuilder().setMsg(msg).build();

        try {
            ServerUUID serverUUID = publishers.get(replicaObjectUUID);
            getStub(serverUUID).handleUnsubscribe(request, emptyResponseObserver);
            changeReceiver.deleteReplica(replicaObjectUUID, new Date(millis));

            // Remove from publishers
            publishers.remove(replicaObjectUUID);
            timestamp.remove(replicaObjectUUID);
            timeout.remove(replicaObjectUUID);

        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed when sending unsubscribe request {0}", e.getStatus());
        }
    }




    /* Functions for Object Location */
    @Override
    public void handleQueryResult(List<edu.rice.rbox.Replication.HolderInfo> interestedObjects) {
        // Subscribe
        interestedObjects.stream()
            .filter(holderInfo -> !publishers.containsKey(holderInfo.getGameObjectUUID()))
            .forEach(holderInfo -> subscribe(holderInfo.getGameObjectUUID(), holderInfo.getServerUUID()));

        // Unsubscribe
        interestedObjects.stream()
            .filter(holderInfo -> publishers.containsKey(holderInfo.getGameObjectUUID()))
            .forEach(holderInfo -> timeout.put(holderInfo.getGameObjectUUID(), initial_value));
        timeout.replaceAll((gameObjectUUID, time) -> time - 1);
        timeout.forEach((gameObjectUUID, time) -> {
            if (time == 0) {
                unsubscribe(gameObjectUUID);
            }
        });
    }

    /* Functions for Object Storage */
    public void updatePrimary(RemoteChange change) {
        logger.log(Level.INFO, "Sending change to primary...");

        // Construct Update Request
        GameObjectUUID targetUuid = change.getTarget();
        ServerUUID serverUUID = publishers.get(targetUuid);
        ByteString changeByteString = getByteStringFromRemoteChange(change);
        RBoxProto.ReplicationMessage msg = generateReplicationMessage(targetUuid);
        RBoxProto.UpdateMessage request = RBoxProto.UpdateMessage.newBuilder()
                                               .setRemoteChange(changeByteString)
                                               .setMsg(msg)
                                               .build();
        try {
            getStub(serverUUID).handleUpdate(request, emptyResponseObserver);

        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "failure when sending update to primary");
        }

    }

    public void broadcastUpdate(RemoteChange change) {
        // Send change to all replica holders
        sendToReplicaHolders(change.getTarget(), change);
    }

    public void deletePrimary(GameObjectUUID primaryObjectUUID, RemoteChange remoteChange) {
        sendToReplicaHolders(primaryObjectUUID, remoteChange);
        subscribers.remove(primaryObjectUUID);
    }

    /* Helper function for update */
    private void sendToReplicaHolders(GameObjectUUID primaryObjectUUID, RemoteChange remoteChange) {
        // Send Update Message to all replica holders
        long millis = System.currentTimeMillis();
        subscribers.get(primaryObjectUUID).forEach(serverUUID -> {
            RBoxProto.ReplicationMessage msg = generateReplicationMessage(primaryObjectUUID, millis);
            RBoxProto.UpdateMessage updateMessage = RBoxProto.UpdateMessage.newBuilder()
                                                   .setRemoteChange(getByteStringFromRemoteChange(remoteChange))
                                                   .setMsg(msg)
                                                   .build();
            try {
                getStub(serverUUID).handleUpdate(updateMessage, emptyResponseObserver);

            } catch (StatusRuntimeException e) {
                logger.log(Level.WARNING, "failure when sending update to replica holders");
            }
        });
    }

    /* Getter for initializing game rooms associated with this superpeer. */
    public List<Integer> getAssignedRooms() { return assignedRooms; }

}
