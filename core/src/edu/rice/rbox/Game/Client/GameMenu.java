package edu.rice.rbox.Game.Client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class GameMenu extends JPanel {

  Menu2Game _menu2Game;
  private final JLabel _title = new JLabel("This is a game");
  private final JButton _playBtn = new JButton("Play");

  public GameMenu(Menu2Game menu2Game) {
    this._menu2Game = menu2Game;
    initMenu();
  }

  private void initMenu() {

    // Menu layout.
    Box box = Box.createVerticalBox();
    box.add(_title);
    box.add(Box.createVerticalStrut(100));
    box.add(_playBtn);

    // Set action listener.
    _playBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // TODO: Need to send some sort of initial message here!
        _menu2Game.playGame();
      }
    });

    add(box);
    setFocusable(true);
  }



}
