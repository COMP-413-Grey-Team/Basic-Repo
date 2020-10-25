package sync_state;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

public class GameState implements Serializable {

  public final HashMap<UUID, SpriteState> gameObjectStates;

  public GameState(HashMap<UUID, SpriteState> gameObjectStates) {
    this.gameObjectStates = gameObjectStates;
  }

}
