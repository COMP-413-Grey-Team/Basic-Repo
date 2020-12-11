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

  private GameClientGrpc clientConnector = new GameClientGrpc();
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
//        System.exit(1);
      }
      clientConnector.connect(ip);

      // Get assigned superpeer
      clientConnector.getSuperPeer(_clientID.toString());

      // FIXME: Will this cause issues?
      GameState response = clientConnector.init(_world.player.getName(), _world.player.getColor().toString());
      _world.handleServerUpdatesAsynchronously(response.playerStates, response.coinStates);

    }

  });

  private final World _world = new World(new PlayerState(30, 30, "Evan", 0),
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

    setTitle("fun game");
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

    setTitle("our fun game!");
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  public static void main(String[] args) {

    EventQueue.invokeLater(() -> {
      JFrame ex = new Game();
      ex.setVisible(true);
    });
  }
}