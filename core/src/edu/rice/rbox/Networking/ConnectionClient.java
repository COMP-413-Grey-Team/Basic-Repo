package edu.rice.rbox.Networking;

import io.grpc.*;
import java.util.concurrent.TimeUnit;
import network.ConnectionServiceGrpc;
import network.Network;

public class ConnectionClient {
  public static void main(String[] args) throws Exception {
    // Channel is the abstraction to connect to a service endpoint
    // Let's use plaintext communication because we don't have certs
    final ManagedChannel channel = ManagedChannelBuilder.forTarget("3.129.207.214:8080")
                                       .usePlaintext(true)
                                       .build();

    // It is up to the client to de
    // termine whether to block the call
    // Here we create a blocking stub, but an async stub,
    // or an async stub with Future are always possible.
    ConnectionServiceGrpc.ConnectionServiceBlockingStub stub = ConnectionServiceGrpc.newBlockingStub(channel);
    Network.ServerInformation request =
        Network.ServerInformation.newBuilder()
            .setServerName("Miguel").setServerType("gang")
            .build();

    // Finally, make the call using the stub
    Network.ConnectionConfirmation response =
        stub.connectTo(request);

    System.out.println(response);

    // A Channel should be shutdown before stopping the process.
    channel.shutdownNow();
  }
}
