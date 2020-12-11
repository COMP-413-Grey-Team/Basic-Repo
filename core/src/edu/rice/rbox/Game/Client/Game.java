package edu.rice.rbox.Game.Client;

import edu.rice.rbox.Game.Common.SyncState.PlayerState;

import edu.rice.rbox.Game.Common.Utils.KeyState;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.UUID;
import javax.swing.*;

public class Game extends JFrame {

  private final GameClientGrpc clientConnector = new GameClientGrpc();
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


  public static void main(String[] args) {

    EventQueue.invokeLater(() -> {
      JFrame ex = new Game();
      ex.setVisible(true);
    });
  }
}