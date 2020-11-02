package edu.rice.comp413fall2020grey.Game.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer extends Thread {

  private final ServerSocket serverSocket;
  ClientConnectionManager clientConnectionManager;

  public GameServer(int port) throws IOException {
    serverSocket = new ServerSocket(port);
    serverSocket.setSoTimeout(10000);
  }

  public void run() {
    while (true) {
      try {
        // TODO: this is all sample code from a tutorial, likely to be replaced by ConnectionServer.
        // The ClientConnectionManager will be responsible for mapping each connection to a client to the GameObjectUUID
        // corresponding to the player that client corresponds to.

        System.out.println("Waiting for client on port " +
                               serverSocket.getLocalPort() + "...");
        Socket server = serverSocket.accept();

        System.out.println("Just connected to " + server.getRemoteSocketAddress());
        DataInputStream in = new DataInputStream(server.getInputStream());

        System.out.println(in.readUTF());
        DataOutputStream out = new DataOutputStream(server.getOutputStream());
        out.writeUTF("Thank you for connecting to " + server.getLocalSocketAddress()
                         + "\nGoodbye!");
        server.close();
      } catch (IOException e) {
        e.printStackTrace();
        break;
      }
    }
  }

  public static void main(String[] args) {
    int port = Integer.parseInt(args[0]);
    try {
      Thread t = new GameServer(port);
      t.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
