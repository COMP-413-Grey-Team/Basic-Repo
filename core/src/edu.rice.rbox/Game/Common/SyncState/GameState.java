package edu.rice.rbox.Game.Common.SyncState;

import java.awt.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

// Sent from server to client each update
public class GameState implements Serializable {

  public final HashMap<UUID, PlayerState> playerStates;
  public final HashMap<UUID, CoinState> coinStates;
  public final Color backgroundColor;

  public GameState(HashMap<UUID, PlayerState> playerStates,
                   HashMap<UUID, CoinState> coinStates,
                   Color backgroundColor) {
    this.playerStates = playerStates;
    this.coinStates = coinStates;
    this.backgroundColor = backgroundColor;
  }

}
