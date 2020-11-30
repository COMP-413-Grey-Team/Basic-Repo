package edu.rice.rbox.Replication;

import com.google.protobuf.Empty;
import edu.rice.rbox.Common.GameObjectUUID;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import network.RBoxProto;
import network.RBoxServiceGrpc;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple client that requests a greeting from the replica manager server.
 * Based on HelloWorldClient.
 */
public class DummyClient {
  private static final Logger logger = Logger.getLogger(DummyClient.class.getName());

  private final RBoxServiceGrpc.RBoxServiceBlockingStub blockingStub;

  /** Construct client for accessing server using the existing channel. */
  public DummyClient(Channel channel) {
    // 'channel' here is a Channel, not a ManagedChannel, so it is not this code's responsibility to
    // shut it down.

    // Passing Channels to code makes code easier to test and makes it easier to reuse Channels.
    blockingStub = RBoxServiceGrpc.newBlockingStub(channel);
  }

  public void unsubscribe(GameObjectUUID replicaObjectUUI) {
    RBoxProto.UnsubscribeRequest request = RBoxProto.UnsubscribeRequest.newBuilder().build();
    Empty response;
    try {
      response = blockingStub.handleUnsubscribe(request);
    } catch (StatusRuntimeException e) {
      logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
      return;
    }
    logger.info("Successfully sent unsubscribe message to server, response: " + response);
  }

  /**
   * Greet server. If provided, the first element of {@code args} is the name to use in the
   * greeting. The second argument is the target server.
   */
  public static void main(String[] args) throws Exception {
    GameObjectUUID replicaObjectUUID = GameObjectUUID.randomUUID();
    // Access a service running on the local machine on port 50051
    String target = "localhost:50051";

    // Create a communication channel to the server, known as a Channel. Channels are thread-safe
    // and reusable. It is common to create channels at the beginning of your application and reuse
    // them until the application shuts down.
    ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                                 // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                                 // needing certificates.
                                 .usePlaintext(true)
                                 .build();
    try {
      DummyClient client = new DummyClient(channel);
      client.unsubscribe(replicaObjectUUID);
    } finally {
      // ManagedChannels use resources like threads and TCP connections. To prevent leaking these
      // resources the channel should be shut down when it will no longer be used. If it may be used
      // again leave it running.
      channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }
  }
}