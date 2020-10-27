package sync_state;

import java.util.HashSet;
import java.util.UUID;

// Sent from client to server
public class GameStateDelta {

  HashSet<UUID> deletedCoins;

  UUID playerUUID;
  PlayerState updatedPlayerState;

}
