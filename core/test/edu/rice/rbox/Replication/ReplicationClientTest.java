package edu.rice.rbox.Replication;

import com.google.protobuf.Empty;
import static org.junit.Assert.assertEquals;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import edu.rice.rbox.Common.Change.RemoteChange;
import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Common.ServerUUID;
import edu.rice.rbox.ObjStorage.ChangeReceiver;
import edu.rice.rbox.Replication.Messages.UnsubscribeMessage;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import network.RBoxProto;
import network.RBoxServiceGrpc;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * Unit tests for ReplicaManager.
 */
@RunWith(JUnit4.class)
public class ReplicationClientTest {
  private Server server;
  private ManagedChannel channel;
  private RBoxServiceGrpc.RBoxServiceBlockingStub blockingStub;
  private RBoxServiceGrpc.RBoxServiceStub asyncStub;


  private static final ChangeReceiver changeReceiver = new ChangeReceiver() {
    @Override
    public void receiveChange(RemoteChange change) { }

    @Override
    public void deleteReplica(GameObjectUUID id, Date timestamp) { }

    @Override
    public RemoteChange getReplica(GameObjectUUID id) {
      return null;
    }

    @Override
    public void promoteSecondary(GameObjectUUID id) { }
  };
  private ServerUUID serverUUID = ServerUUID.randomUUID();
  private ReplicaManagerGrpc client;

  // Mock server (ReplicaManagerImpl)
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
  public void beforeEachTest() throws IOException {
    channel = InProcessChannelBuilder
                  .forName("test")
                  .directExecutor()
                  // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                  // needing certificates.
                  .usePlaintext(true)
                  .build();
    blockingStub = RBoxServiceGrpc.newBlockingStub(channel);
    asyncStub = RBoxServiceGrpc.newStub(channel);

    server = InProcessServerBuilder
                 .forName(serverUUID.toString())
                 .directExecutor()
                 .addService(replicaManagerImpl)
                 .build().start();
    client = new ReplicaManagerGrpc(changeReceiver, serverUUID);
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
    // fail the test if cannot gracefully shutdown
    try {
      assert channel.awaitTermination(5, TimeUnit.SECONDS) : "channel cannot be gracefully shutdown";
    } finally {
      channel.shutdownNow();
    }
  }
}