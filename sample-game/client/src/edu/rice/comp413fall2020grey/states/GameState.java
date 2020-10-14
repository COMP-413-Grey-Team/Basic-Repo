package edu.rice.comp413fall2020grey.states;

import edu.rice.comp413fall2020grey.utils.KeyHandler;
import edu.rice.comp413fall2020grey.utils.MouseHandler;

import java.awt.*;

public abstract class GameState {

  protected GameStateManager manager;

  public GameState(GameStateManager mgr) {
    manager = mgr;
  }

  public abstract void update();
  public abstract void input(MouseHandler mouse, KeyHandler key);
  public abstract void render(Graphics2D g);

}
