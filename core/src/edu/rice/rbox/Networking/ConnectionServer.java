//package edu.rice.rbox.Networking;
//
//import io.grpc.*;
//import io.grpc.netty.NettyServerBuilder;
//
//import java.net.InetAddress;
//
//import java.net.DatagramSocket;
//
//public class ConnectionServer {
//  public static void main( String[] args ) throws Exception {
//
//
//    String ip = "";
//    try(final DatagramSocket socket = new DatagramSocket()){
//      socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
//      ip = socket.getLocalAddress().getHostAddress();
//    }
//
//    System.out.println("Server Running on address: " + ip);
//
//
//    // Create a new server to listen on port 8080
//
//    Server server = ServerBuilder.forPort(8080)
//                        .addService(new NetworkImpl())
//                        .build();
//
//
//    // Start the server
//    server.start();
//
//    // Server threads are running in the background.
//    System.out.println("Server started");
//
//    // Don't exit the main thread. Wait until server is terminated.
//    server.awaitTermination();
//  }
//
//
//}