package edu.rice.comp413fall2020grey.Game.Common.SyncState;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

// Sent from server to client each update
public class GameState implements Serializable {

  public final HashMap<UUID, PlayerState> playerStates;
  public final HashMap<UUID, CoinState> coinStates;

  public GameState(HashMap<UUID, PlayerState>
                       playerStates,
                   HashMap<UUID, CoinState> coinStates) {
    this.playerStates = playerStates;
    this.coinStates = coinStates;
  }

}
