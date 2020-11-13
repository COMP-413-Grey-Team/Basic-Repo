package edu.rice.rbox.Game.Common.SyncState;

import edu.rice.rbox.Common.GameObjectUUID;

import java.awt.*;
import java.io.Serializable;
import java.util.Map;

// Sent from server to client each update
public class GameState implements Serializable {

  public final GameObjectUUID clientUUID;
  public final Map<GameObjectUUID, PlayerState> playerStates;
  public final Map<GameObjectUUID, CoinState> coinStates;
  public final Color backgroundColor;

  public GameState(GameObjectUUID clientUUID,
                   Map<GameObjectUUID, PlayerState> playerStates,
                   Map<GameObjectUUID, CoinState> coinStates,
                   Color backgroundColor) {
    this.clientUUID = clientUUID;
    this.playerStates = playerStates;
    this.coinStates = coinStates;
    this.backgroundColor = backgroundColor;
  }

}
