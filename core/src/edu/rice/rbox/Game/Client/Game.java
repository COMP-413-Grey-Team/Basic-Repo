package edu.rice.rbox.Game.Client;

import edu.rice.rbox.Game.Common.SyncState.PlayerState;

import edu.rice.rbox.Game.Common.Utils.KeyState;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;

public class Game extends JFrame {

  private final JPanel _contentPane = new JPanel();
  private final GameMenu _menu = new GameMenu(new Menu2Game() {
    @Override
    public void playGame() {
      initGame();
    }
  });
  private final World _world = new World(new PlayerState(30, 30, "Evan", Color.BLUE, 0));

  public Game() {
    setContentPane(_contentPane);
    initMenu();
  }

  private void initMenu() {
    _contentPane.add(_menu);
    System.out.println(_world.hasFocus());
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