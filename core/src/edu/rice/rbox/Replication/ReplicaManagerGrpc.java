package edu.rice.rbox.Replication;

import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
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


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
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
    private HashMap<GameObjectUUID, Integer> timeout = new HashMap<>();                   // Primary => timeout
    private Integer initial_value = 50;

    private HashMap<ServerUUID, RBoxServiceGrpc.RBoxServiceBlockingStub> blockingStubs = new HashMap<>();
    private HashMap<ServerUUID, RBoxServiceGrpc.RBoxServiceStub> stubs = new HashMap<>();
    private RegistrarGrpc.RegistrarBlockingStub registrarBlockingStub;

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
            RemoteChange change = getRemoteChangeFromUpdateMessage(request);
            changeReceiver.receiveChange(change);

            responseObserver.onCompleted();
        }
    };

    private RegistrarGrpc.RegistrarImplBase registrarServiceImpl = new RegistrarGrpc.RegistrarImplBase() {
        @Override
        public void alert(RBoxProto.NewRegistrarMessage request, StreamObserver<Empty> responseObserver) {
            // Save Registrar blocing stub
            String registrarIP = request.getNewRegistrarIP();
            ManagedChannel channel = ManagedChannelBuilder.forTarget(registrarIP)
                                         .usePlaintext(true)
                                         .build();
            registrarBlockingStub = RegistrarGrpc.newBlockingStub(channel);
            responseObserver.onCompleted();
        }

        @Override
        public void promote(RBoxProto.PromoteSecondaryMessage request, StreamObserver<Empty> responseObserver) {
            request.getPromotedUUIDsList().forEach(uuidStr -> {
                GameObjectUUID gameObjectUUID = new GameObjectUUID(UUID.fromString(uuidStr));
                changeReceiver.promoteSecondary(gameObjectUUID);
                // No longer treated as a replica
                publishers.remove(gameObjectUUID);
                timeout.remove(gameObjectUUID);
            });
            responseObserver.onCompleted();
        }

        @Override
        public void connect(RBoxProto.ConnectMessage request, StreamObserver<Empty> responseObserver) {
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

            responseObserver.onCompleted();
        }

        @Override
        public void querySecondary(RBoxProto.querySecondaryMessage request, StreamObserver<RBoxProto.secondaryTimestampsMessage> responseObserver) {
            // No-op
        }


        @Override
        public void assignGameRooms(network.RBoxProto.GameRooms request,
                                    io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
            // TODO: IMPLEMENT THIS PEOPLES!!!
        }
    };

    /* Constructor */
    public ReplicaManagerGrpc(int port, ChangeReceiver changeReceiver, ServerUUID serverUUID) {
        this.changeReceiver = changeReceiver;
        this.serverUUID = serverUUID;
        this.port = port;
        this.server = ServerBuilder.forPort(port)
                          .addService(rboxServiceImpl)
                          .addService(registrarServiceImpl)
                          .build();
    }

    /* Start the server */
    public void start(String registrarIP) throws Exception {
        server.start();
        logger.info("Server started, listening on " + port);
        logger.info("This server UUID is " + serverUUID);
        logger.info("Attempt to connect to " + registrarIP);

        ManagedChannel channel = ManagedChannelBuilder.forTarget(registrarIP).usePlaintext(true).build();
        this.registrarBlockingStub = RegistrarGrpc.newBlockingStub(channel);

        // TODO: Find public IP address - UNTESTED
        String systemipaddress = "";
        URL url_name = new URL("http://bot.whatismyipaddress.com");
        BufferedReader sc =
            new BufferedReader(new InputStreamReader(url_name.openStream()));
        systemipaddress = sc.readLine().trim();


        // Send the registrar a Connect message - Need the other files!
        long millis = System.currentTimeMillis();

        RBoxProto.ConnectMessage request =
            RBoxProto.ConnectMessage.newBuilder()
                .setConnectionIP(systemipaddress + ":8080")
                .setSender(generateBasicInfo(millis))
                .build();


        registrarBlockingStub.connect(request);
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

            // Add into publishers
            publishers.put(remoteChange.getTarget(), serverUUID);
            timeout.put(remoteChange.getTarget(), initial_value);

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

}
