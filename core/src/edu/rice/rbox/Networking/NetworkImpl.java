package edu.rice.rbox.Networking;

import io.grpc.stub.StreamObserver;
import network.ConnectionServiceGrpc;
import network.Network;

public class NetworkImpl extends ConnectionServiceGrpc.ConnectionServiceImplBase {


  @Override
  public void connectTo(Network.ServerInformation request, StreamObserver<Network.ConnectionConfirmation> responseObserver) {
    System.out.println("Server that must be connected to: " + request.getServerName());

    System.out.println("HERE'S WHERE WE CONNECT");

    // build up response
    Network.ConnectionConfirmation response = Network.ConnectionConfirmation.newBuilder()
                                                  .setConfirmation("Confirmed, my dude").build();

    // send response i guess
    responseObserver.onNext(response);


    // signify that transaction is over
    responseObserver.onCompleted();
  }

  // CONSTRUCTOR FOR MESSAGE
  public NetworkImpl() {
  }

}
