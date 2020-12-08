package edu.rice.rbox.Location.registrar;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import network.InternalRegistrarFaultToleranceGrpc;
import network.InternalRegistrarFaultToleranceGrpc.*;
import network.RBoxProto;

// this class will manage connection to registrar cluster members, providing fault tolerance if/when
// the leader goes down
public class ClusterManager {

  // holds the stubs to cluster members
  protected Map<InternalRegistrarFaultToleranceBlockingStub, UUID> clusterMemberStubs;
  protected Map<UUID, InternalRegistrarFaultToleranceBlockingStub> clusterMemberUUIDs;

  InternalRegistrarFaultToleranceBlockingStub leaderStub;

  UUID leaderUUID;

  // this is the ip:port that the server running this service is running on
  private String hostName;

  private UUID serverRunningThisUUID;

  private Consumer<String> alertSuperpeersOfNewLeader;

  private boolean leader = false;

  private Timestamp mostRecentHeartbeat = Timestamp.getDefaultInstance();

  public Timestamp getMostRecentHeartbeat() {
    return mostRecentHeartbeat;
  }

  private boolean receivedInitialHeartbeat = false;

  private int numLeaderMsg = 0;

  private void setNumLeaderMsg(int i) {
    numLeaderMsg = i;
  }

  private int getNumLeaderMsg() {
    return numLeaderMsg;
  }

  private int getNumRegistrarNodes() {
    return clusterMemberStubs.size();
  }

  private String getUUID() {
    return serverRunningThisUUID.toString();
  }

  private Timestamp getTimestamp() {
    long millis = System.currentTimeMillis();
    Timestamp timestamp = Timestamp.newBuilder().setSeconds(millis / 1000)
            .setNanos((int) ((millis % 1000) * 1000000)).build();
    return timestamp;
  }

  private RBoxProto.BasicInfo getInfo() {
    return RBoxProto.BasicInfo.newBuilder().setSenderUUID(getUUID()).setTime(getTimestamp()).build();
  }

  private Timestamp mostRecentDownedLeader = getTimestamp();

  // implements the internal registrar fault tolerance service
  private InternalRegistrarFaultToleranceGrpc.InternalRegistrarFaultToleranceImplBase internalClusterServiceImpl =
      new InternalRegistrarFaultToleranceImplBase() {

    @Override
    public void alert(RBoxProto.NewRegistrarMessage request, StreamObserver<Empty> responseObserver) {
      //TODO
      leaderUUID = UUID.fromString(request.getSender().getSenderUUID());
      responseObserver.onNext(Empty.getDefaultInstance());
    }

    @Override
    public void connectFromCluster(RBoxProto.ConnectMessage request, StreamObserver<Empty> responseObserver) {
      //TODO: create the gRPC blocking stub for the internal service for the machine running on the ip:port in the request of this RPC
      String senderUUID = request.getClusterMemberSender().getSenderUUID();
      ManagedChannel channel = ManagedChannelBuilder.forTarget(senderUUID)
              .usePlaintext(true)
              .build();
      InternalRegistrarFaultToleranceBlockingStub stub2clusterMember = InternalRegistrarFaultToleranceGrpc.newBlockingStub(channel);
      clusterMemberStubs.put(stub2clusterMember, UUID.fromString(senderUUID));
      clusterMemberUUIDs.put(UUID.fromString(senderUUID), stub2clusterMember);
      /*String senderUUID = request.getSender().getSenderUUID();
      String senderHostnameInfo = request.getConnectionIP();

      TheCoolRegistrar.this.connManager.addSuperPeer(senderHostnameInfo);*/
    }

    @Override
    public void checkConnectionToClusterMember(RBoxProto.CheckConnection request,
                                               StreamObserver<RBoxProto.ConnectionResult> responseObserver) {
      //TODO
      String target = request.getCheckUUID();
      InternalRegistrarFaultToleranceBlockingStub stub = clusterMemberUUIDs.get(target);

      RBoxProto.HeartBeatRequest req = RBoxProto.HeartBeatRequest.newBuilder().setSender(getInfo()).build();

      boolean success;
      try {
        stub.heartBeatClusterMember(req);
        success = true;
      } catch (Exception ex) {
        success = false;
        responseObserver.onError(ex);
      }

      RBoxProto.ConnectionResult res = RBoxProto.ConnectionResult.newBuilder().setSender(getInfo()).setResult(success).build();
      responseObserver.onNext(res);
      responseObserver.onCompleted();
    }

    @Override
    public void heartBeatClusterMember(RBoxProto.HeartBeatRequest request,
                                       StreamObserver<RBoxProto.HeartBeatResponse> responseObserver) {
      //TODO
      if(!receivedInitialHeartbeat) {
        receivedInitialHeartbeat = true;
      }
      mostRecentHeartbeat = getTimestamp();
      responseObserver.onNext(RBoxProto.HeartBeatResponse.newBuilder().setSender(getInfo()).setStatus(RBoxProto.HeartBeatResponse.ServingStatus.SERVING).build());
    }

    @Override
    public void downedLeader(RBoxProto.LeaderDown request, StreamObserver<Empty> responseObserver) {
      //TODO
      if (!(getTimestamp().getSeconds() <= mostRecentDownedLeader.getSeconds() + 1)) {
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

  // getter for the anon inner class implementation of the gRPC service
  public InternalRegistrarFaultToleranceGrpc.InternalRegistrarFaultToleranceImplBase getInternalServiceImpl() {
    return this.internalClusterServiceImpl;
  }



  // constructor
  public ClusterManager(String serverThisIsRunningOn, UUID uuidOfServerRunningThis,
                        Consumer<String> alertNewLeader) {
    this.hostName = serverThisIsRunningOn;
    this.serverRunningThisUUID = uuidOfServerRunningThis;
    this.alertSuperpeersOfNewLeader = alertNewLeader;
  }




  public void makeClusterInterconnected() {
    /**
     * for each cluster member i
     *    for each cluster member j that is not i
     *        send a connect message to j with the information from i
     * return nothing
     * */


  }

}
