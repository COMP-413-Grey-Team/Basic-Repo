package core.src.edu.rice.comp413fall2020grey.Networking;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;

/**
 * <pre>
 * Defining a Service, a Service can have multiple RPC operations
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.7.0)",
    comments = "Source: network.proto")
public final class ConnectionServiceGrpc {

  private ConnectionServiceGrpc() {}

  public static final String SERVICE_NAME = "core.src.edu.rice.comp413fall2020grey.Networking.ConnectionService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<core.src.edu.rice.comp413fall2020grey.Networking.Network.ServerInformation,
      core.src.edu.rice.comp413fall2020grey.Networking.Network.ConnectionConfirmation> METHOD_CONNECT_TO =
      io.grpc.MethodDescriptor.<core.src.edu.rice.comp413fall2020grey.Networking.Network.ServerInformation, core.src.edu.rice.comp413fall2020grey.Networking.Network.ConnectionConfirmation>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "core.src.edu.rice.comp413fall2020grey.Networking.ConnectionService", "connectTo"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              core.src.edu.rice.comp413fall2020grey.Networking.Network.ServerInformation.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              core.src.edu.rice.comp413fall2020grey.Networking.Network.ConnectionConfirmation.getDefaultInstance()))
          .setSchemaDescriptor(new ConnectionServiceMethodDescriptorSupplier("connectTo"))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ConnectionServiceStub newStub(io.grpc.Channel channel) {
    return new ConnectionServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ConnectionServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new ConnectionServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ConnectionServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new ConnectionServiceFutureStub(channel);
  }

  /**
   * <pre>
   * Defining a Service, a Service can have multiple RPC operations
   * </pre>
   */
  public static abstract class ConnectionServiceImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * Define a RPC operation
     * </pre>
     */
    public void connectTo(core.src.edu.rice.comp413fall2020grey.Networking.Network.ServerInformation request,
        io.grpc.stub.StreamObserver<core.src.edu.rice.comp413fall2020grey.Networking.Network.ConnectionConfirmation> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_CONNECT_TO, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_CONNECT_TO,
            asyncUnaryCall(
              new MethodHandlers<
                core.src.edu.rice.comp413fall2020grey.Networking.Network.ServerInformation,
                core.src.edu.rice.comp413fall2020grey.Networking.Network.ConnectionConfirmation>(
                  this, METHODID_CONNECT_TO)))
          .build();
    }
  }

  /**
   * <pre>
   * Defining a Service, a Service can have multiple RPC operations
   * </pre>
   */
  public static final class ConnectionServiceStub extends io.grpc.stub.AbstractStub<ConnectionServiceStub> {
    private ConnectionServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ConnectionServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ConnectionServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ConnectionServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Define a RPC operation
     * </pre>
     */
    public void connectTo(core.src.edu.rice.comp413fall2020grey.Networking.Network.ServerInformation request,
        io.grpc.stub.StreamObserver<core.src.edu.rice.comp413fall2020grey.Networking.Network.ConnectionConfirmation> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_CONNECT_TO, getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   * Defining a Service, a Service can have multiple RPC operations
   * </pre>
   */
  public static final class ConnectionServiceBlockingStub extends io.grpc.stub.AbstractStub<ConnectionServiceBlockingStub> {
    private ConnectionServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ConnectionServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ConnectionServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ConnectionServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Define a RPC operation
     * </pre>
     */
    public core.src.edu.rice.comp413fall2020grey.Networking.Network.ConnectionConfirmation connectTo(core.src.edu.rice.comp413fall2020grey.Networking.Network.ServerInformation request) {
      return blockingUnaryCall(
          getChannel(), METHOD_CONNECT_TO, getCallOptions(), request);
    }
  }

  /**
   * <pre>
   * Defining a Service, a Service can have multiple RPC operations
   * </pre>
   */
  public static final class ConnectionServiceFutureStub extends io.grpc.stub.AbstractStub<ConnectionServiceFutureStub> {
    private ConnectionServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ConnectionServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ConnectionServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ConnectionServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Define a RPC operation
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<core.src.edu.rice.comp413fall2020grey.Networking.Network.ConnectionConfirmation> connectTo(
        core.src.edu.rice.comp413fall2020grey.Networking.Network.ServerInformation request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_CONNECT_TO, getCallOptions()), request);
    }
  }

  private static final int METHODID_CONNECT_TO = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ConnectionServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ConnectionServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CONNECT_TO:
          serviceImpl.connectTo((core.src.edu.rice.comp413fall2020grey.Networking.Network.ServerInformation) request,
              (io.grpc.stub.StreamObserver<core.src.edu.rice.comp413fall2020grey.Networking.Network.ConnectionConfirmation>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class ConnectionServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ConnectionServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return core.src.edu.rice.comp413fall2020grey.Networking.Network.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ConnectionService");
    }
  }

  private static final class ConnectionServiceFileDescriptorSupplier
      extends ConnectionServiceBaseDescriptorSupplier {
    ConnectionServiceFileDescriptorSupplier() {}
  }

  private static final class ConnectionServiceMethodDescriptorSupplier
      extends ConnectionServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ConnectionServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (ConnectionServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ConnectionServiceFileDescriptorSupplier())
              .addMethod(METHOD_CONNECT_TO)
              .build();
        }
      }
    }
    return result;
  }
}
