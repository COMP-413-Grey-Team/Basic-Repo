package edu.rice.rbox.Networking.GrpcUtils;

import io.grpc.BindableService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import network.RegistrarGrpc;

public class SuperPeerServer {

  private Server server;

  /**
   * Constructor for the superpeer's Grpc server
   * @param services this is a list of all the implemented services that this server will use (defines RPCs that
   *                 can be called on it)
   */
  public SuperPeerServer (List<BindableService> services) {
    var temp =  ServerBuilder.forPort(8080);
    services.forEach((s) -> temp.addService(s));
    this.server = temp.build();
  }

  /**
   * This method starts up the system from the superpeer's perspective. It does the following:
   * Starts up its own gRPC server
   * Creates a gRPC client for registrar and returns it
   * Sends the registrar a connectTo message
   * @param registrarIP the IP and port number that the registrar's server is running on (in the string form
   *                    "ip:port")
   */
  public void startUp(String registrarIP) throws Exception {
    // start up the server
    this.server.start();

    // make channel
    ManagedChannel channel = ManagedChannelBuilder.forTarget(registrarIP)
                                 .usePlaintext(true)
                                 .build();

    // create the grpc client
    RegistrarGrpc.RegistrarBlockingStub stub2registrar = RegistrarGrpc.newBlockingStub(channel);

    // Find public IP address - UNTESTED
    String systemipaddress = "";

    URL url_name = new URL("http://bot.whatismyipaddress.com");

    BufferedReader sc =
        new BufferedReader(new InputStreamReader(url_name.openStream()));

    systemipaddress = sc.readLine().trim();


    // send the registrar a Connect message - Need the other files!

    stub2registrar.connect();


  }



}
