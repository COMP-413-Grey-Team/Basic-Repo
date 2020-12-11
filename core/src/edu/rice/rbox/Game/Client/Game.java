package edu.rice.rbox.Game.Client;

import edu.rice.rbox.Game.Common.SyncState.PlayerState;
import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Game.Common.SyncState.CoinState;
import edu.rice.rbox.Game.Common.SyncState.GameState;
import edu.rice.rbox.Game.Common.SyncState.GameStateDelta;
import edu.rice.rbox.Game.Common.SyncState.PlayerState;
import edu.rice.rbox.Game.Client.Messages.UpdateFromClientMessage;
import edu.rice.rbox.Game.Server.Messages.UpdateFromServerMessage;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.awt.*;
import java.util.UUID;
import java.util.stream.Collectors;

import network.GameNetworkProto;
import network.GameServiceGrpc;
import edu.rice.rbox.Game.Common.Utils.KeyState;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.UUID;
import javax.swing.*;

public class Game extends JFrame {

  private ManagedChannel channel;
  private GameServiceGrpc.GameServiceBlockingStub registrarStub;
  private GameServiceGrpc.GameServiceBlockingStub gamerServerStub;

  private final GameClientGrpc clientConnector = new GameClientGrpc();
//  private final GameClient clientConnector = new GameClient() {
//
//    @Override
//    public void connect(String ip) {
//
//    }
//
//    @Override
//    public GameNetworkProto.UpdateFromServer update(GameStateDelta gsd) {
//      return null;
//    }
//
//    @Override
//    public GameNetworkProto.UpdateFromServer init(String name, String color) {
//      return null;
//    }
//
//    @Override
//    public GameNetworkProto.SuperPeerInfo getSuperPeer(String playerID) {
//      return null;
//    }
//
//    @Override
//    public void remove(GameObjectUUID playerID) {
//
//    }
//  };
  private final JPanel _contentPane = new JPanel();
  private final GameMenu _menu = new GameMenu(new Menu2Game() {
    @Override
    public void playGame() {
      initGame();
    }

    @Override
    public void connectToRegistrar(String ip) {
      if (ip.length() == 0) {
        System.err.println("Error: Empty IP was empty!");
        System.exit(1);
      }
      clientConnector.connect(ip);

      // Get assigned superpeer
      clientConnector.getSuperPeer(_clientID.toString());
    }

  });
  private final World _world = new World(new PlayerState(30, 30, "Evan", Color.BLUE, 0),
      clientConnector);
  private final UUID _clientID = UUID.randomUUID();

  public Game() {
    setContentPane(_contentPane);
    initMenu();
  }

  private void initMenu() {
    _contentPane.add(_menu);
    setResizable(false);
    pack();

    setTitle("snake");
    setLocationRelativeTo(null);
  }

  private void initGame() {
    _contentPane.removeAll();
    _contentPane.add(_world);
    _world.initWorld();
    _world.requestFocusInWindow();
    _contentPane.setVisible(true);
    _contentPane.revalidate();
    setResizable(false);
    pack();

    setTitle("Snake");
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

//  /**
//   * This is a helper.
//   */
//  private  PlayerState reconstructPlayerState(GameNetworkProto.PlayerMessage msg) {
//    return new PlayerState(msg.getX(), msg.getY(),
//            msg.getName(), new Color(Integer.parseInt(msg.getColor())),
//            Integer.parseInt(msg.getScore()));
//  }
//
//  /**
//   * This is a helper.
//   */
//  public GameState serverMsgToGameState(UpdateFromServerMessage msg) {
//    GameNetworkProto.UpdateFromServer update = msg.getUpdateFromServer();
//    return new GameState(new GameObjectUUID(UUID.fromString(update.getPlayerUUID())),
//            update.getPlayerStatesMap().entrySet().stream().collect(Collectors.toMap(
//                    e -> new GameObjectUUID(UUID.fromString(e.getKey())),
//                    e -> reconstructPlayerState(e.getValue()))),
//            update.getCoinStatesMap().entrySet().stream().collect(Collectors.toMap(
//                    e -> new GameObjectUUID(UUID.fromString(e.getKey())),
//                    e -> new CoinState(e.getValue().getX(), e.getValue().getY()))),
//            new Color(Integer.parseInt(update.getWorldColor())));
//  }
//
//  /**
//   * This is a seemingly unused helper.
//   */
//  public UpdateFromClientMessage gameStateDeltaToClientMsg(GameStateDelta gsd) {
//    return new UpdateFromClientMessage(gsd.playerUUID, gsd.updatedPlayerState,
//            gsd.deletedCoins,
//            gsd.movingRooms.getNumber());
//  }


  public static void main(String[] args) {

    EventQueue.invokeLater(() -> {
      JFrame ex = new Game();
      ex.setVisible(true);
    });
  }
}