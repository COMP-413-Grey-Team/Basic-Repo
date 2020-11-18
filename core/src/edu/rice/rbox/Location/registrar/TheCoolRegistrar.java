package edu.rice.rbox.Location.registrar;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import edu.rice.rbox.Networking.NetworkImpl;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import io.grpc.stub.StreamObserver;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

import network.ElectionGrpc;
import network.FaultToleranceGrpc;
import network.HealthGrpc;
import network.RBoxProto;
import network.RBoxServiceGrpc;
import network.RegistrarGrpc;

public class TheCoolRegistrar {

  private TheCoolConnectionManager connManager;

  private RegistrarGrpc.RegistrarImplBase superPeerServiceImpl = new RegistrarGrpc.RegistrarImplBase() {

    @Override
    public void alert(RBoxProto.NewRegistrarMessage request, StreamObserver<Empty> responseObserver) {
      // TODO: Nothing, because the registrar knows if its the lead or not already
    }

    @Override
    public void promote(RBoxProto.PromoteSecondaryMessage request, StreamObserver<Empty> responseObserver) {
      // TODO: idk wtf this is supposed to do
    }

    @Override
    public void connect(RBoxProto.ConnectMessage request, StreamObserver<Empty> responseObserver) {
      // TODO: this is the UUID
      String senderUUID = request.getSender().getSenderUUID();
      String senderHostnameInfo = request.getConnectionIP();

      TheCoolRegistrar.this.connManager.addSuperPeer(senderHostnameInfo);
    }

    @Override
    public void querySecondary(RBoxProto.querySecondaryMessage request,
                               StreamObserver<RBoxProto.secondaryTimestampsMessage> responseObserver) {
      // TODO: ditto to not knowing what this one does either
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

  //TODO: change this to work with the rest of the registrar
  private ElectionGrpc.ElectionImplBase electionServiceImpl = new ElectionGrpc.ElectionImplBase() {
    int numLeaderMsg = 0;
    HashMap<String, ElectionGrpc.ElectionBlockingStub> stubmap;
    String uuid = "";
    public String getUUID() {
      return this.uuid;
    }

    public int getNumLeaderMsg() {
      return numLeaderMsg;
    }

    public void setNumLeaderMsg(int numLeaderMsg) {
      this.numLeaderMsg = numLeaderMsg;
    }

    public Timestamp getTimestamp() {
      long millis = System.currentTimeMillis();
      Timestamp timestamp = Timestamp.newBuilder().setSeconds(millis / 1000)
              .setNanos((int) ((millis % 1000) * 1000000)).build();
      return timestamp;
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
    }

    @Override
    public void check(FaultToleranceGrpc.CheckIn request, StreamObserver<FaultToleranceGrpc.Info> responseObserver) {
      FaultToleranceGrpc.Info information = FaultToleranceGrpc.Info.newBuilder().setSenderUUID(getUUID()).setTime(getTimestamp()).build();
      responseObserver.onNext(information);
    }

    @Override
    public void downedLeader(FaultToleranceGrpc.LeaderDown request, StreamObserver<Empty> responseObserver) {
      this.numLeaderMsg++;
      responseObserver.onNext(Empty.newBuilder().build());
    }
  };




  public TheCoolRegistrar() {
    // TODO: set up the connection manager / Mongo stuff
    this.connManager = new TheCoolConnectionManager(null, null);
  }

  public void init() throws  Exception {
    String ip = "";
    try(final DatagramSocket socket = new DatagramSocket()){
      socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
      ip = socket.getLocalAddress().getHostAddress();
    }

    System.out.println("Server Running on address: " + ip);

    // TODO: Setup Mongo
    // TODO: Create Connection Manager
    // TODO: Start grpc server with proper services

    // Create a new server to listen on port 8080

    Server server = ServerBuilder.forPort(8080)
                        // this is for registrar/player client interactions
                        .addService(this.connManager.getGameServerRegistrarImpl())
                        // this is for registrar/superpeer interactions
                        .addService(this.superPeerServiceImpl)
                        // TODO: this is for the registrar faults/elections - looking @ u Nikhaz
                        .addService(this.healthServiceImpl)
                        // TODO: this is for the health service @ Nikhaz
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



  }
}
