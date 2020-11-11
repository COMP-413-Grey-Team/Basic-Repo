package edu.rice.rbox.Game.Server;

import edu.rice.rbox.Common.Change.LocalAddReplicaChange;
import edu.rice.rbox.Common.Change.LocalChange;
import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Game.Common.SyncState.GameState;
import edu.rice.rbox.Game.Common.SyncState.GameStateDelta;
import edu.rice.rbox.Game.Common.SyncState.PlayerState;
import edu.rice.rbox.ObjStorage.ObjectStore;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GameServer extends Thread {

  private static final String KEY_TYPE = "RESERVED_TYPE";
  private static final String KEY_TYPE_PLAYER = "RESERVED_TYPE_PLAYER";
  private static final String KEY_TYPE_COIN = "RESERVED_TYPE_COIN";
  private static final String KEY_TYPE_ROOM = "RESERVED_TYPE_ROOM";

  private final ServerSocket serverSocket;
  ClientConnectionManager clientConnectionManager;
  private ObjectStore objectStore;
  private GameStateManager manager = new GameStateManager();

  private ArrayList<GameStateDelta> clientUpdates;

  public GameServer(int port) throws IOException {
    serverSocket = new ServerSocket(port);
    serverSocket.setSoTimeout(10000);
  }

  public void run() {
//    while (true) {
//
//      final Set<LocalChange> localChanges = objectStore.synchronize();
//      localChanges.forEach(change -> {
//        for (int i = change.getBufferIndex(); i >= 0; i--) {
//          objectStore.write(change.copyWithIndex(i), change.getTarget());
//        }
//      });
//      objectStore.advanceBuffer();
//
//      // Process all game updates deltas
//      clientUpdates.forEach(manager::handleUpdateFromPlayer);
//
//      // TODO: send out game state to clients
//      HashMap<GameObjectUUID, GameState> roomToGameStateMemoizer = new HashMap<>();
//      for (GameObjectUUID playerUUID : clientConnectionManager.playersThisServerIsResponsibleFor) {
//        GameObjectUUID roomUUID = manager.roomForPlayer(playerUUID);
//        if (!roomToGameStateMemoizer.containsKey(roomUUID)) {
//          final Map<GameObjectUUID, PlayerState> playerStates =
//              manager.playersInRoom(roomUUID).stream().collect(Collectors.toMap(
//                 player -> player,
//                 player -> manager.playerStateForPlayer(player)
//              ));
//          new GameState()
//        }
//      }
//    }
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