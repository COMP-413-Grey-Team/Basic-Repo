package edu.rice.rbox.Location.registrar;

import com.google.protobuf.Empty;
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
  private Map<InternalRegistrarFaultToleranceBlockingStub, UUID> clusterMemberStubs;

  // this is the ip:port that the server running this service is running on
  private String hostName;

  private UUID serverRunningThisUUID;

  private Consumer<String> alertSuperpeersOfNewLeader;

  // implements the internal registrar fault tolerance service
  private InternalRegistrarFaultToleranceGrpc.InternalRegistrarFaultToleranceImplBase internalClusterServiceImpl =
      new InternalRegistrarFaultToleranceImplBase() {

    @Override
    public void alert(RBoxProto.NewRegistrarMessage request, StreamObserver<Empty> responseObserver) {
      //TODO
    }

    @Override
    public void connectFromCluster(RBoxProto.ConnectMessage request, StreamObserver<Empty> responseObserver) {
      //TODO: create the gRPC blocking stub for the internal service for the machine running on the ip:port in the request of this RPC

      String senderUUID = request.getClusterMemberSender().getSenderUUID();
      InternalRegistrarFaultToleranceBlockingStub stub2clusterMember;
    }

    @Override
    public void checkConnectionToClusterMember(RBoxProto.CheckConnection request,
                                               StreamObserver<RBoxProto.ConnectionResult> responseObserver) {
      //TODO
    }

    @Override
    public void heartBeatClusterMember(RBoxProto.HeartBeatRequest request,
                                       StreamObserver<RBoxProto.HeartBeatResponse> responseObserver) {
      //TODO
    }

    @Override
    public void downedLeader(RBoxProto.LeaderDown request, StreamObserver<Empty> responseObserver) {
      //TODO
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
