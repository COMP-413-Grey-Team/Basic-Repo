package edu.rice.rbox.Game.Common.SyncState;

import edu.rice.rbox.Common.GameObjectUUID;

import java.awt.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

// Sent from server to client each update
public class GameState implements Serializable {

  public final HashMap<GameObjectUUID, PlayerState> playerStates;
  public final HashMap<GameObjectUUID, CoinState> coinStates;
  public final Color backgroundColor;

  public GameState(HashMap<GameObjectUUID, PlayerState> playerStates,
                   HashMap<GameObjectUUID, CoinState> coinStates,
                   Color backgroundColor) {
    this.playerStates = playerStates;
    this.coinStates = coinStates;
    this.backgroundColor = backgroundColor;
  }

}
