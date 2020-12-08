package edu.rice.rbox.Location.registrar;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.internal.connection.ClusterableServerFactory;
import edu.rice.rbox.Location.Mongo.MongoManager;
import edu.rice.rbox.Networking.NetworkImpl;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import io.grpc.stub.StreamObserver;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.UUID;


import java.util.function.Consumer;
import network.*;
import network.ElectionGrpc;
import network.HealthGrpc;
import network.RBoxServiceGrpc;
import network.RegistrarConnectionsGrpc;

public class TheCoolRegistrar {

  private TheCoolConnectionManager connManager;

  private ClusterManager clusterManager;

  boolean leader = false;

  protected UUID uuid;

  private UUID leaderUUID;

  public String getUUID() {
    return uuid.toString();
  }

  private RBoxProto.BasicInfo getInfo() {
    return RBoxProto.BasicInfo.newBuilder().setSenderUUID(getUUID()).setTime(getTimestamp()).build();
  }

  protected Timestamp getTimestamp() {
    long millis = System.currentTimeMillis();
    Timestamp timestamp = Timestamp.newBuilder().setSeconds(millis / 1000)
            .setNanos((int) ((millis % 1000) * 1000000)).build();
    return timestamp;
  }

  private static MongoManager mongoManager;

  private static MongoClient mongoClient;

  private static ClientSession clientSession;

  String REGISTRAR = "registrar_DB";

  public TheCoolRegistrar(String password) {
    // TODO: set up the connection manager / Mongo stuff
    this.connManager = new TheCoolConnectionManager(null, null);
    mongoManager = new MongoManager(password);
  }

  public void init() throws  Exception {
    String ip = "";
    try(final DatagramSocket socket = new DatagramSocket()){
      socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
      ip = socket.getLocalAddress().getHostAddress();
    }

    // TODO: Setup Mongo
    // TODO: Create Connection Manager
    // TODO: Start grpc server with proper services

    mongoManager.connect();
    mongoClient = mongoManager.getMongoClient();
    clientSession = mongoClient.startSession();

    System.out.println("Server Running on address: " + ip);
    // Create a new server to listen on port 8080

    Server server = ServerBuilder.forPort(8080)
                        // this is for registrar/player client interactions
                        .addService(this.connManager.getGameServerRegistrarImpl())
                        // this is for registrar/superpeer interactions
                        .addService(this.clusterManager.getInternalServiceImpl())
                        .addService((BindableService) null)
                        .build();

    // Start the server
    server.start();

    // Server threads are running in the background.
    System.out.println("Server started");

    // Don't exit the main thread. Wait until server is terminated.
    server.awaitTermination();
  }


  public static void main( String[] args ) throws Exception {

    //start server by calling init

    // create a new thread

    // that thread will run a while loop forever

    // that while loop will send the heartbeats

    var thing = new ClusterManager(null, null, new Consumer<String>() {
      @Override
      public void accept(String s) {
//        for (Stub : connManager.superpees) {
//          send somehing
//        }
      }
    })
  }
}
