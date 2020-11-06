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
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.7.0)",
    comments = "Source: gameserver.proto")
public final class PlayerServerGrpc {

  private PlayerServerGrpc() {}

  public static final String SERVICE_NAME = "PlayerServer";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<GameNetworkProto.UpdateFromClient,
      GameNetworkProto.UpdateFromServer> METHOD_PUBLISH_UPDATE =
      io.grpc.MethodDescriptor.<GameNetworkProto.UpdateFromClient, GameNetworkProto.UpdateFromServer>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "PlayerServer", "publishUpdate"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              GameNetworkProto.UpdateFromClient.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              GameNetworkProto.UpdateFromServer.getDefaultInstance()))
          .setSchemaDescriptor(new PlayerServerMethodDescriptorSupplier("publishUpdate"))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<GameNetworkProto.Empty,
      GameNetworkProto.SuperPeerInfo> METHOD_GET_ASSIGNED_SUPER_PEER =
      io.grpc.MethodDescriptor.<GameNetworkProto.Empty, GameNetworkProto.SuperPeerInfo>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "PlayerServer", "getAssignedSuperPeer"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              GameNetworkProto.Empty.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              GameNetworkProto.SuperPeerInfo.getDefaultInstance()))
          .setSchemaDescriptor(new PlayerServerMethodDescriptorSupplier("getAssignedSuperPeer"))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<GameNetworkProto.InitialPlayerState,
      GameNetworkProto.UpdateFromServer> METHOD_INIT_PLAYER =
      io.grpc.MethodDescriptor.<GameNetworkProto.InitialPlayerState, GameNetworkProto.UpdateFromServer>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "PlayerServer", "initPlayer"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              GameNetworkProto.InitialPlayerState.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              GameNetworkProto.UpdateFromServer.getDefaultInstance()))
          .setSchemaDescriptor(new PlayerServerMethodDescriptorSupplier("initPlayer"))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<GameNetworkProto.PlayerID,
      GameNetworkProto.Empty> METHOD_REMOVE_ME =
      io.grpc.MethodDescriptor.<GameNetworkProto.PlayerID, GameNetworkProto.Empty>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "PlayerServer", "removeMe"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              GameNetworkProto.PlayerID.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              GameNetworkProto.Empty.getDefaultInstance()))
          .setSchemaDescriptor(new PlayerServerMethodDescriptorSupplier("removeMe"))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static PlayerServerStub newStub(io.grpc.Channel channel) {
    return new PlayerServerStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static PlayerServerBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new PlayerServerBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static PlayerServerFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new PlayerServerFutureStub(channel);
  }

  /**
   */
  public static abstract class PlayerServerImplBase implements io.grpc.BindableService {

    /**
     */
    public void publishUpdate(GameNetworkProto.UpdateFromClient request,
        io.grpc.stub.StreamObserver<GameNetworkProto.UpdateFromServer> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_PUBLISH_UPDATE, responseObserver);
    }

    /**
     */
    public void getAssignedSuperPeer(GameNetworkProto.Empty request,
        io.grpc.stub.StreamObserver<GameNetworkProto.SuperPeerInfo> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_GET_ASSIGNED_SUPER_PEER, responseObserver);
    }

    /**
     */
    public void initPlayer(GameNetworkProto.InitialPlayerState request,
        io.grpc.stub.StreamObserver<GameNetworkProto.UpdateFromServer> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_INIT_PLAYER, responseObserver);
    }

    /**
     */
    public void removeMe(GameNetworkProto.PlayerID request,
        io.grpc.stub.StreamObserver<GameNetworkProto.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_REMOVE_ME, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_PUBLISH_UPDATE,
            asyncUnaryCall(
              new MethodHandlers<
                GameNetworkProto.UpdateFromClient,
                GameNetworkProto.UpdateFromServer>(
                  this, METHODID_PUBLISH_UPDATE)))
          .addMethod(
            METHOD_GET_ASSIGNED_SUPER_PEER,
            asyncUnaryCall(
              new MethodHandlers<
                GameNetworkProto.Empty,
                GameNetworkProto.SuperPeerInfo>(
                  this, METHODID_GET_ASSIGNED_SUPER_PEER)))
          .addMethod(
            METHOD_INIT_PLAYER,
            asyncUnaryCall(
              new MethodHandlers<
                GameNetworkProto.InitialPlayerState,
                GameNetworkProto.UpdateFromServer>(
                  this, METHODID_INIT_PLAYER)))
          .addMethod(
            METHOD_REMOVE_ME,
            asyncUnaryCall(
              new MethodHandlers<
                GameNetworkProto.PlayerID,
                GameNetworkProto.Empty>(
                  this, METHODID_REMOVE_ME)))
          .build();
    }
  }

  /**
   */
  public static final class PlayerServerStub extends io.grpc.stub.AbstractStub<PlayerServerStub> {
    private PlayerServerStub(io.grpc.Channel channel) {
      super(channel);
    }

    private PlayerServerStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PlayerServerStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new PlayerServerStub(channel, callOptions);
    }

    /**
     */
    public void publishUpdate(GameNetworkProto.UpdateFromClient request,
        io.grpc.stub.StreamObserver<GameNetworkProto.UpdateFromServer> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_PUBLISH_UPDATE, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getAssignedSuperPeer(GameNetworkProto.Empty request,
        io.grpc.stub.StreamObserver<GameNetworkProto.SuperPeerInfo> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_GET_ASSIGNED_SUPER_PEER, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void initPlayer(GameNetworkProto.InitialPlayerState request,
        io.grpc.stub.StreamObserver<GameNetworkProto.UpdateFromServer> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_INIT_PLAYER, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void removeMe(GameNetworkProto.PlayerID request,
        io.grpc.stub.StreamObserver<GameNetworkProto.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_REMOVE_ME, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class PlayerServerBlockingStub extends io.grpc.stub.AbstractStub<PlayerServerBlockingStub> {
    private PlayerServerBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private PlayerServerBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PlayerServerBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new PlayerServerBlockingStub(channel, callOptions);
    }

    /**
     */
    public GameNetworkProto.UpdateFromServer publishUpdate(GameNetworkProto.UpdateFromClient request) {
      return blockingUnaryCall(
          getChannel(), METHOD_PUBLISH_UPDATE, getCallOptions(), request);
    }

    /**
     */
    public GameNetworkProto.SuperPeerInfo getAssignedSuperPeer(GameNetworkProto.Empty request) {
      return blockingUnaryCall(
          getChannel(), METHOD_GET_ASSIGNED_SUPER_PEER, getCallOptions(), request);
    }

    /**
     */
    public GameNetworkProto.UpdateFromServer initPlayer(GameNetworkProto.InitialPlayerState request) {
      return blockingUnaryCall(
          getChannel(), METHOD_INIT_PLAYER, getCallOptions(), request);
    }

    /**
     */
    public GameNetworkProto.Empty removeMe(GameNetworkProto.PlayerID request) {
      return blockingUnaryCall(
          getChannel(), METHOD_REMOVE_ME, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class PlayerServerFutureStub extends io.grpc.stub.AbstractStub<PlayerServerFutureStub> {
    private PlayerServerFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private PlayerServerFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PlayerServerFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new PlayerServerFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<GameNetworkProto.UpdateFromServer> publishUpdate(
        GameNetworkProto.UpdateFromClient request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_PUBLISH_UPDATE, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<GameNetworkProto.SuperPeerInfo> getAssignedSuperPeer(
        GameNetworkProto.Empty request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_GET_ASSIGNED_SUPER_PEER, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<GameNetworkProto.UpdateFromServer> initPlayer(
        GameNetworkProto.InitialPlayerState request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_INIT_PLAYER, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<GameNetworkProto.Empty> removeMe(
        GameNetworkProto.PlayerID request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_REMOVE_ME, getCallOptions()), request);
    }
  }

  private static final int METHODID_PUBLISH_UPDATE = 0;
  private static final int METHODID_GET_ASSIGNED_SUPER_PEER = 1;
  private static final int METHODID_INIT_PLAYER = 2;
  private static final int METHODID_REMOVE_ME = 3;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final PlayerServerImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(PlayerServerImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_PUBLISH_UPDATE:
          serviceImpl.publishUpdate((GameNetworkProto.UpdateFromClient) request,
              (io.grpc.stub.StreamObserver<GameNetworkProto.UpdateFromServer>) responseObserver);
          break;
        case METHODID_GET_ASSIGNED_SUPER_PEER:
          serviceImpl.getAssignedSuperPeer((GameNetworkProto.Empty) request,
              (io.grpc.stub.StreamObserver<GameNetworkProto.SuperPeerInfo>) responseObserver);
          break;
        case METHODID_INIT_PLAYER:
          serviceImpl.initPlayer((GameNetworkProto.InitialPlayerState) request,
              (io.grpc.stub.StreamObserver<GameNetworkProto.UpdateFromServer>) responseObserver);
          break;
        case METHODID_REMOVE_ME:
          serviceImpl.removeMe((GameNetworkProto.PlayerID) request,
              (io.grpc.stub.StreamObserver<GameNetworkProto.Empty>) responseObserver);
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

  private static abstract class PlayerServerBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    PlayerServerBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return GameNetworkProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("PlayerServer");
    }
  }

  private static final class PlayerServerFileDescriptorSupplier
      extends PlayerServerBaseDescriptorSupplier {
    PlayerServerFileDescriptorSupplier() {}
  }

  private static final class PlayerServerMethodDescriptorSupplier
      extends PlayerServerBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    PlayerServerMethodDescriptorSupplier(String methodName) {
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
      synchronized (PlayerServerGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new PlayerServerFileDescriptorSupplier())
              .addMethod(METHOD_PUBLISH_UPDATE)
              .addMethod(METHOD_GET_ASSIGNED_SUPER_PEER)
              .addMethod(METHOD_INIT_PLAYER)
              .addMethod(METHOD_REMOVE_ME)
              .build();
        }
      }
    }
    return result;
  }
}
