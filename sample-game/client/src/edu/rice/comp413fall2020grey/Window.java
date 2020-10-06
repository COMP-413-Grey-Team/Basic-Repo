package edu.rice.comp413fall2020grey;

import javax.swing.*;

public class Window extends JFrame {

  public Window() {
    setTitle("Temp Title");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setContentPane(new GamePanel(1280, 720));
    pack();
    setLocationRelativeTo(null);
    setVisible(true);
  }

}
