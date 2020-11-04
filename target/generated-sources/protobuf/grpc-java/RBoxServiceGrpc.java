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
 * this is the ONLY service defined (all RPCs go here)
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.7.0)",
    comments = "Source: rbox.proto")
public final class RBoxServiceGrpc {

  private RBoxServiceGrpc() {}

  public static final String SERVICE_NAME = "RBoxService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<Rbox.SubscribeRequest,
      Rbox.UpdateMessage> METHOD_HANDLE_SUBSCRIBE =
      io.grpc.MethodDescriptor.<Rbox.SubscribeRequest, Rbox.UpdateMessage>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "RBoxService", "handleSubscribe"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              Rbox.SubscribeRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              Rbox.UpdateMessage.getDefaultInstance()))
          .setSchemaDescriptor(new RBoxServiceMethodDescriptorSupplier("handleSubscribe"))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<Rbox.UpdateMessage,
      com.google.protobuf.Empty> METHOD_HANDLE_UPDATE =
      io.grpc.MethodDescriptor.<Rbox.UpdateMessage, com.google.protobuf.Empty>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "RBoxService", "handleUpdate"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              Rbox.UpdateMessage.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.google.protobuf.Empty.getDefaultInstance()))
          .setSchemaDescriptor(new RBoxServiceMethodDescriptorSupplier("handleUpdate"))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<Rbox.UnsubscribeRequest,
      com.google.protobuf.Empty> METHOD_HANDLE_UNSUBSCRIBE =
      io.grpc.MethodDescriptor.<Rbox.UnsubscribeRequest, com.google.protobuf.Empty>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "RBoxService", "handleUnsubscribe"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              Rbox.UnsubscribeRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.google.protobuf.Empty.getDefaultInstance()))
          .setSchemaDescriptor(new RBoxServiceMethodDescriptorSupplier("handleUnsubscribe"))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static RBoxServiceStub newStub(io.grpc.Channel channel) {
    return new RBoxServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static RBoxServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new RBoxServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static RBoxServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new RBoxServiceFutureStub(channel);
  }

  /**
   * <pre>
   * this is the ONLY service defined (all RPCs go here)
   * </pre>
   */
  public static abstract class RBoxServiceImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * Replication
     * </pre>
     */
    public void handleSubscribe(Rbox.SubscribeRequest request,
        io.grpc.stub.StreamObserver<Rbox.UpdateMessage> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_HANDLE_SUBSCRIBE, responseObserver);
    }

    /**
     */
    public void handleUpdate(Rbox.UpdateMessage request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_HANDLE_UPDATE, responseObserver);
    }

    /**
     */
    public void handleUnsubscribe(Rbox.UnsubscribeRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_HANDLE_UNSUBSCRIBE, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_HANDLE_SUBSCRIBE,
            asyncUnaryCall(
              new MethodHandlers<
                Rbox.SubscribeRequest,
                Rbox.UpdateMessage>(
                  this, METHODID_HANDLE_SUBSCRIBE)))
          .addMethod(
            METHOD_HANDLE_UPDATE,
            asyncUnaryCall(
              new MethodHandlers<
                Rbox.UpdateMessage,
                com.google.protobuf.Empty>(
                  this, METHODID_HANDLE_UPDATE)))
          .addMethod(
            METHOD_HANDLE_UNSUBSCRIBE,
            asyncUnaryCall(
              new MethodHandlers<
                Rbox.UnsubscribeRequest,
                com.google.protobuf.Empty>(
                  this, METHODID_HANDLE_UNSUBSCRIBE)))
          .build();
    }
  }

  /**
   * <pre>
   * this is the ONLY service defined (all RPCs go here)
   * </pre>
   */
  public static final class RBoxServiceStub extends io.grpc.stub.AbstractStub<RBoxServiceStub> {
    private RBoxServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private RBoxServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RBoxServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new RBoxServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Replication
     * </pre>
     */
    public void handleSubscribe(Rbox.SubscribeRequest request,
        io.grpc.stub.StreamObserver<Rbox.UpdateMessage> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_HANDLE_SUBSCRIBE, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void handleUpdate(Rbox.UpdateMessage request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_HANDLE_UPDATE, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void handleUnsubscribe(Rbox.UnsubscribeRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_HANDLE_UNSUBSCRIBE, getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   * this is the ONLY service defined (all RPCs go here)
   * </pre>
   */
  public static final class RBoxServiceBlockingStub extends io.grpc.stub.AbstractStub<RBoxServiceBlockingStub> {
    private RBoxServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private RBoxServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RBoxServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new RBoxServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Replication
     * </pre>
     */
    public Rbox.UpdateMessage handleSubscribe(Rbox.SubscribeRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_HANDLE_SUBSCRIBE, getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty handleUpdate(Rbox.UpdateMessage request) {
      return blockingUnaryCall(
          getChannel(), METHOD_HANDLE_UPDATE, getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty handleUnsubscribe(Rbox.UnsubscribeRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_HANDLE_UNSUBSCRIBE, getCallOptions(), request);
    }
  }

  /**
   * <pre>
   * this is the ONLY service defined (all RPCs go here)
   * </pre>
   */
  public static final class RBoxServiceFutureStub extends io.grpc.stub.AbstractStub<RBoxServiceFutureStub> {
    private RBoxServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private RBoxServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RBoxServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new RBoxServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Replication
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<Rbox.UpdateMessage> handleSubscribe(
        Rbox.SubscribeRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_HANDLE_SUBSCRIBE, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> handleUpdate(
        Rbox.UpdateMessage request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_HANDLE_UPDATE, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> handleUnsubscribe(
        Rbox.UnsubscribeRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_HANDLE_UNSUBSCRIBE, getCallOptions()), request);
    }
  }

  private static final int METHODID_HANDLE_SUBSCRIBE = 0;
  private static final int METHODID_HANDLE_UPDATE = 1;
  private static final int METHODID_HANDLE_UNSUBSCRIBE = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final RBoxServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(RBoxServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_HANDLE_SUBSCRIBE:
          serviceImpl.handleSubscribe((Rbox.SubscribeRequest) request,
              (io.grpc.stub.StreamObserver<Rbox.UpdateMessage>) responseObserver);
          break;
        case METHODID_HANDLE_UPDATE:
          serviceImpl.handleUpdate((Rbox.UpdateMessage) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_HANDLE_UNSUBSCRIBE:
          serviceImpl.handleUnsubscribe((Rbox.UnsubscribeRequest) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
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

  private static abstract class RBoxServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    RBoxServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return Rbox.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("RBoxService");
    }
  }

  private static final class RBoxServiceFileDescriptorSupplier
      extends RBoxServiceBaseDescriptorSupplier {
    RBoxServiceFileDescriptorSupplier() {}
  }

  private static final class RBoxServiceMethodDescriptorSupplier
      extends RBoxServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    RBoxServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (RBoxServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new RBoxServiceFileDescriptorSupplier())
              .addMethod(METHOD_HANDLE_SUBSCRIBE)
              .addMethod(METHOD_HANDLE_UPDATE)
              .addMethod(METHOD_HANDLE_UNSUBSCRIBE)
              .build();
        }
      }
    }
    return result;
  }
}
