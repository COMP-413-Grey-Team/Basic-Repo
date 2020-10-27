package sync_state;

import java.util.HashSet;
import java.util.UUID;

// Sent from client to server
public class GameStateDelta {

  public UUID playerUUID;
  public PlayerState updatedPlayerState;

  public HashSet<UUID> deletedCoins;

  public GameStateDelta(UUID playerUUID, PlayerState updatedPlayerState, HashSet<UUID> deletedCoins) {
    this.playerUUID = playerUUID;
    this.updatedPlayerState = updatedPlayerState;
    this.deletedCoins = deletedCoins;
  }

}
