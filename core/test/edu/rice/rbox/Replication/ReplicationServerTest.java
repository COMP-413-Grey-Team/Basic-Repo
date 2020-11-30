package edu.rice.rbox.Replication;

import static org.junit.Assert.assertEquals;

import com.google.protobuf.Empty;
import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Replication.DummyServer.GreeterImpl;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import network.RBoxProto;
import network.RBoxServiceGrpc;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Unit tests for Replica Manager server.
 */
@RunWith(JUnit4.class)
public class ReplicationServerTest {
  private Server server;
  private ManagedChannel channel;
  private RBoxServiceGrpc.RBoxServiceBlockingStub blockingStub;

  @Before
  public void beforeEachTest() throws IOException {
    // Create a server, add service, and start it.
    server = InProcessServerBuilder
                 .forName("ReplicationServerTest")
                 .directExecutor()
                 .addService(new GreeterImpl())
                 .build().start();

    // Create a client channel
    channel = InProcessChannelBuilder
                  .forName("ReplicationServerTest")
                  .directExecutor()
                  .build();

    blockingStub = RBoxServiceGrpc.newBlockingStub(channel);
  }

  /**
   * To test the server, make calls with a real stub using the in-process channel, and verify
   * behaviors or state changes from the client side.
   */
  @Test
  public void greeterImpl_subscribeMessage() throws Exception {
    GameObjectUUID objectUUID = GameObjectUUID.randomUUID();
    RBoxProto.ReplicationMessage msg = RBoxProto.ReplicationMessage.newBuilder()
                                            .setTargetObjectUUID(objectUUID.toString())
                                            .build();
    RBoxProto.UpdateMessage reply =
        blockingStub.handleSubscribe(RBoxProto.SubscribeRequest.newBuilder().setMsg(msg).build());

    assertEquals(objectUUID.toString(), reply.getMsg().getTargetObjectUUID());
  }
  @Test
  public void greeterImpl_unsubscribeMessage() throws Exception {
    // TODO: set fields in message
    RBoxProto.ReplicationMessage msg = RBoxProto.ReplicationMessage.newBuilder().build();
    Empty reply =
        blockingStub.handleUnsubscribe(RBoxProto.UnsubscribeRequest.newBuilder().setMsg(msg).build());

    assertEquals(Empty.newBuilder().build(), reply);
  }

  /**
   * Clean up gRPC resources. Fail the test if cleanup is not successful.
   * From https://grpc.io/blog/graceful-cleanup-junit-tests/ as an alternative to GrpcCleanupRule.
   */
  @After
  public void tearDown() throws InterruptedException {
    // assume channel and server are not null
    channel.shutdown();
    // fail the test if cannot gracefully shutdown
    try {
      assert channel.awaitTermination(5, TimeUnit.SECONDS) : "channel cannot be gracefully shutdown";
    } finally {
      channel.shutdownNow();
    }
  }
}
