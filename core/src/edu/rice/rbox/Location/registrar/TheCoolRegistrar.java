package edu.rice.rbox.Location.registrar;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
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


import network.*;
import network.ElectionGrpc;
import network.HealthGrpc;
import network.RBoxServiceGrpc;
import network.RegistrarConnectionsGrpc;

public class TheCoolRegistrar {

  private TheCoolConnectionManager connManager;

  boolean leader = false;

  protected UUID uuid;

  private UUID leaderUUID;

  public String getUUID() {
    return uuid.toString();
  }

  private RBoxProto.BasicInfo getInfo() {
    return RBoxProto.BasicInfo.newBuilder().setSenderUUID(getUUID()).setTime(getTimestamp()).build();
  }

  private RegistrarConnectionsGrpc.RegistrarConnectionsImplBase registrarConnectionServiceImpl = new RegistrarConnectionsGrpc.RegistrarConnectionsImplBase() {

    @Override
    public void alert(RBoxProto.NewRegistrarMessage request, StreamObserver<Empty> responseObserver) {
      // TODO: Nothing, because the registrar knows if its the lead or not already
      leaderUUID = UUID.fromString(request.getSender().getSenderUUID());
      responseObserver.onNext(Empty.getDefaultInstance());
    }

    @Override
    public void connect(RBoxProto.ConnectMessage request, StreamObserver<Empty> responseObserver) {
      // TODO: this is the UUID
      String senderUUID = request.getSender().getSenderUUID();
      String senderHostnameInfo = request.getConnectionIP();

      TheCoolRegistrar.this.connManager.addSuperPeer(senderHostnameInfo);
    }
  };

  private HealthGrpc.HealthImplBase healthServiceImpl = new HealthGrpc.HealthImplBase() {
    @Override
    public void check(RBoxProto.HeartBeatRequest request, StreamObserver<RBoxProto.HeartBeatResponse> responseObserver) {
      responseObserver.onNext(RBoxProto.HeartBeatResponse.newBuilder().setSender(getInfo()).setStatus(RBoxProto.HeartBeatResponse.ServingStatus.SERVING).build());
    }
  };


  static int numLeaderMsg = 0;

  protected Timestamp getTimestamp() {
    long millis = System.currentTimeMillis();
    Timestamp timestamp = Timestamp.newBuilder().setSeconds(millis / 1000)
            .setNanos((int) ((millis % 1000) * 1000000)).build();
    return timestamp;
  }

  protected double getNumRegistrarNodes() {
    return (double)mongoClient.getDatabase(REGISTRAR).getCollection("registrar").countDocuments(clientSession);
  }

  protected Timestamp mostRecentDL = getTimestamp();

  //TODO: change this to work with the rest of the registrar
  private ElectionGrpc.ElectionImplBase electionServiceImpl = new ElectionGrpc.ElectionImplBase() {
    HashMap<String, ElectionGrpc.ElectionBlockingStub> stubmap;

    public int getNumLeaderMsg() {
      return numLeaderMsg;
    }

    public void setNumLeaderMsg(int newNumLeaderMsg) {
      numLeaderMsg = newNumLeaderMsg;
    }

    @Override
    public void connection(FaultToleranceGrpc.CheckConnection request,
                           StreamObserver<FaultToleranceGrpc.ConnectionResult> responseObserver) {
      String target = request.getCheckUUID();
      ElectionGrpc.ElectionBlockingStub stub = this.stubmap.get(target);

      FaultToleranceGrpc.Info information = FaultToleranceGrpc.Info.newBuilder().setSenderUUID(getUUID()).setTime(getTimestamp()).build();
      FaultToleranceGrpc.CheckIn req = FaultToleranceGrpc.CheckIn.newBuilder().setSender(information).build();

      boolean success;
      try {
        stub.check(req);
        success = true;
      } catch (Exception ex) {
        success = false;
        responseObserver.onError(ex);
      }

      FaultToleranceGrpc.ConnectionResult res = FaultToleranceGrpc.ConnectionResult.newBuilder().setSender(information).setResult(success).build();
      responseObserver.onNext(res);
      responseObserver.onCompleted();
    }

    @Override
    public void check(FaultToleranceGrpc.CheckIn request, StreamObserver<FaultToleranceGrpc.Info> responseObserver) {
      FaultToleranceGrpc.Info information = FaultToleranceGrpc.Info.newBuilder().setSenderUUID(getUUID()).setTime(getTimestamp()).build();
      responseObserver.onNext(information);
      responseObserver.onCompleted();
    }

    @Override
    public void downedLeader(FaultToleranceGrpc.LeaderDown request, StreamObserver<Empty> responseObserver) {
      if (!(getTimestamp().getSeconds() <= mostRecentDL.getSeconds() + 1)) {
        setNumLeaderMsg(0);
      }
      setNumLeaderMsg(getNumLeaderMsg() + 1);
      if (getNumLeaderMsg() > (2.0 / 3.0) * getNumRegistrarNodes()) {
        leader = true;

      }
      responseObserver.onNext(Empty.newBuilder().build());
      responseObserver.onCompleted();
    }
  };

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
                        .addService(this.registrarConnectionServiceImpl)
                        // TODO: this is for the registrar faults/elections - looking @ u Nikhaz
                        .addService(this.healthServiceImpl)
                        // TODO: this is for the health service @ Nikhaz
                        .addService(this.electionServiceImpl)
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
  }
}
