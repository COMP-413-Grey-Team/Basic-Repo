package edu.rice.rbox.Game.Server;

import edu.rice.rbox.Common.Change.LocalAddReplicaChange;
import edu.rice.rbox.Common.Change.LocalChange;
import edu.rice.rbox.ObjStorage.ObjectStore;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;

public class GameServer extends Thread {

  private final ServerSocket serverSocket;
  ClientConnectionManager clientConnectionManager;
  private ObjectStore objectStore;

  public GameServer(int port) throws IOException {
    serverSocket = new ServerSocket(port);
    serverSocket.setSoTimeout(10000);
  }

  public void run() {
    while (true) {

      final Set<LocalChange> localChanges = objectStore.synchronize();
      localChanges.forEach(change -> {
        if (change instanceof LocalAddReplicaChange) { // Add this new object to my local data
          LocalAddReplicaChange larc = (LocalAddReplicaChange) change;

        }
      });
      objectStore.advanceBuffer();

      // TODO: game logic.

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
