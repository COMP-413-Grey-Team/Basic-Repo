package edu.rice.rbox.Networking.GrpcUtils;

import com.google.protobuf.Timestamp;
import io.grpc.BindableService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import network.RBoxProto;
import network.SuperpeerFaultToleranceGrpc;

public class SuperPeerServer {

  private Server server;

  private UUID serverUUID;

  /**
   * Constructor for the superpeer's Grpc server
   * @param services this is a list of all the implemented services that this server will use (defines RPCs that
   *                 can be called on it)
   * @param serverUUID this is the UUID of the superpeer that this server will be running on
   */
  public SuperPeerServer (List<BindableService> services, UUID serverUUID) {
    var temp =  ServerBuilder.forPort(8080);
    services.forEach((s) -> temp.addService(s));
    this.server = temp.build();
    this.serverUUID = serverUUID;
  }

  /**
   * This method starts up the system from the superpeer's perspective. It does the following:
   * Starts up its own gRPC server
   * Creates a gRPC client for registrar and returns it
   * Sends the registrar a connectTo message, so the registrar now has a stub to this superpeer's server
   *
   * @param registrarIP the IP and port number that the registrar's server is running on (in the string form
   *                    "ip:port")
   * @return a stub to the registrar
   */
  public SuperpeerFaultToleranceGrpc.SuperpeerFaultToleranceBlockingStub startUp(String registrarIP) throws Exception {
    // start up the server
    this.server.start();

    // make channel to the registrar using its IP and port num
    ManagedChannel channel = ManagedChannelBuilder.forTarget(registrarIP)
                                 .usePlaintext(true)
                                 .build();

    // create the grpc client
    SuperpeerFaultToleranceGrpc.SuperpeerFaultToleranceBlockingStub stub2registrar = SuperpeerFaultToleranceGrpc.newBlockingStub(channel);

    // Find public IP address - UNTESTED
    String systemipaddress = "";
    URL url_name = new URL("http://bot.whatismyipaddress.com");
    BufferedReader sc =
        new BufferedReader(new InputStreamReader(url_name.openStream()));
    systemipaddress = sc.readLine().trim();


    // send the registrar a Connect message - Need the other files!
    long millis = System.currentTimeMillis();
    Timestamp timestamp = Timestamp.newBuilder().setSeconds(millis / 1000)
                              .setNanos((int) ((millis % 1000) * 1000000)).build();

    RBoxProto.BasicInfo info = RBoxProto.BasicInfo.newBuilder()
                                   .setSenderUUID(this.serverUUID.toString())
                                   .setTime(timestamp)
                                   .build();

    RBoxProto.ConnectMessage request =
        RBoxProto.ConnectMessage.newBuilder()
            .setConnectionIP(systemipaddress + ":8080")
            .setSender(info)
            .build();


    stub2registrar.connectToSuperpeer(request);

    return stub2registrar;
  }




}
