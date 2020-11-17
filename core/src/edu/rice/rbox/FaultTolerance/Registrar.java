package edu.rice.rbox.FaultTolerance;

import edu.rice.rbox.Networking.NetworkImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class Registrar {
    public static void main( String[] args ) throws Exception {


        String ip = "";
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            ip = socket.getLocalAddress().getHostAddress();
        }

        System.out.println("Server Running on address: " + ip);

        // TODO: Setup Mongo
        // TODO: Create Connection Manager
        // TODO: Start grpc server with proper services

        // Create a new server to listen on port 8080

        Server server = ServerBuilder.forPort(8080)
                            .addService(new NetworkImpl())
                            .build();


        // Start the server
        server.start();

        // Server threads are running in the background.
        System.out.println("Server started");

        // Don't exit the main thread. Wait until server is terminated.
        server.awaitTermination();
    }
}
