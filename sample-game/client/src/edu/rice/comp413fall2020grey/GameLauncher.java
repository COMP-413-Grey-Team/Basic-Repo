package edu.rice.comp413fall2020grey;

public class GameLauncher implements Runnable {

  public GameLauncher() {
    new Window();
  }

  public static void main(String[] args) {
    new GameLauncher();
  }

  @Override
  public void run() {}
}
