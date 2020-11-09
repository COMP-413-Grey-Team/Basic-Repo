//package edu.rice.rbox.Game.Client;
//
//import edu.rice.rbox.Game.Common.SyncState.PlayerState;
//
//import java.awt.*;
//import javax.swing.JFrame;
//
//public class Game extends JFrame {
//
//  public Game() {
//
//    initUI();
//  }
//
//  private void initUI() {
//    add(new World(new PlayerState(30, 30, "Evan", Color.BLUE, 0)));
//
//    setResizable(false);
//    pack();
//
//    setTitle("Snake");
//    setLocationRelativeTo(null);
//    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//  }
//
//  public static void main(String[] args) {
//
//    EventQueue.invokeLater(() -> {
//      JFrame ex = new Game();
//      ex.setVisible(true);
//    });
//  }
//}