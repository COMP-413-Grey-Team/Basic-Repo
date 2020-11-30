package edu.rice.rbox.Replication;

import com.google.protobuf.Empty;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import network.RBoxProto;
import network.RBoxServiceGrpc;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Server that manages startup/shutdown of a dummy Replica Manager server.
 * Based on HelloWorldServer from gRPC java example.
 */
public class DummyServer {
  private static final Logger logger = Logger.getLogger(DummyServer.class.getName());

  private Server server;

  private void start() throws IOException {
    /* The port on which the server should run */
    int port = 50051;
    server = ServerBuilder.forPort(port)
                 .addService(new GreeterImpl())
                 .build()
                 .start();
    logger.info("Server started, listening on " + port);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        try {
          DummyServer.this.stop();
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
        }
        System.err.println("*** server shut down");
      }
    });
  }

  private void stop() throws InterruptedException {
    if (server != null) {
      server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
  }

  /**
   * Await termination on the main thread since the grpc library uses daemon threads.
   */
  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }

  /**
   * Main launches the server from the command line.
   */
  public static void main(String[] args) throws IOException, InterruptedException {
    final DummyServer server = new DummyServer();
    server.start();
    server.blockUntilShutdown();
  }

  static class GreeterImpl extends RBoxServiceGrpc.RBoxServiceImplBase {
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
}
