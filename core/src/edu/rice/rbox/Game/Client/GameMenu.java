package edu.rice.rbox.Game.Client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class GameMenu extends JPanel {

  private final JPanel _content = new JPanel();
  private final JLabel _title = new JLabel("Tumbly Fumbles");
  private final JButton _playBtn = new JButton("Play");

  public GameMenu() {
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
        // TODO: Send add player message here?

      }
    });

    add(box);
    setFocusable(true);
  }



}
