package edu.rice.rbox.Replication;

import com.google.protobuf.Empty;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.mock;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.stub.StreamObserver;
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
 * Unit tests for ReplicaManager.
 */
@RunWith(JUnit4.class)
public class ReplicationTest {
  private Server server = new Server;
  private ManagedChannel channel;
  private RBoxServiceGrpc.RBoxServiceBlockingStub blockingStub;
  private RBoxServiceGrpc.RBoxServiceStub asyncStub;

  // Mock ReplicaManagerImpl
  private static final RBoxServiceGrpc.RBoxServiceImplBase replicaManagerImpl =
      mock(RBoxServiceGrpc.RBoxServiceImplBase.class, delegatesTo(
          new RBoxServiceGrpc.RBoxServiceImplBase() {
            @Override
            public void handleSubscribe(RBoxProto.SubscribeRequest request,
                                        StreamObserver<RBoxProto.UpdateMessage> responseObserver) {
              responseObserver.onNext(RBoxProto.UpdateMessage.getDefaultInstance());
              responseObserver.onCompleted();
            }

            @Override
            public void handleUnsubscribe(RBoxProto.UnsubscribeRequest request,
                                          StreamObserver<Empty> responseObserver) {
              responseObserver.onNext(Empty.getDefaultInstance());
              responseObserver.onCompleted();
            }

            @Override
            public void handleUpdate(RBoxProto.UpdateMessage request, StreamObserver<Empty> responseObserver) {
              responseObserver.onNext(Empty.getDefaultInstance());
              responseObserver.onCompleted();
            }
          }
      ));

  @Before
  public void beforeEachTest() throws InstantiationException, IllegalAccessException, IOException {
    channel = InProcessChannelBuilder
                  .forName("test")
                  .directExecutor()
                  .usePlaintext(true)
                  .build();
    blockingStub = RBoxServiceGrpc.newBlockingStub(channel);
    asyncStub = RBoxServiceGrpc.newStub(channel);
  }

  /**
   * Send message to fake server, and verify behaviors from the server side.
   */
  @Test
  public void testSubscribeMsg() {
    // TODO
  }

  @Test
  public void testUnsubscribeMsg() {
    // TODO
  }

  @Test
  public void testUpdate() {
    // TODO
  }

  /**
   * Clean up gRPC resources. Fail the test if cleanup is not successful.
   * From https://grpc.io/blog/graceful-cleanup-junit-tests/ as an alternative to GrpcCleanupRule.
   */
  @After
  public void tearDown() throws InterruptedException {
    // assume channel and server are not null
    channel.shutdown();
    server.shutdown();
    // fail the test if cannot gracefully shutdown
    try {
      assert channel.awaitTermination(5, TimeUnit.SECONDS) : "channel cannot be gracefully shutdown";
      assert server.awaitTermination(5, TimeUnit.SECONDS) : "server cannot be gracefully shutdown";
    } finally {
      channel.shutdownNow();
      server.shutdownNow();
    }
  }
}