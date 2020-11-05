package edu.rice.rbox.Game.Common.Utils;

import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Game.Common.SyncState.PlayerState;
import edu.rice.rbox.Protos.Generated.GameNetworkProto.UpdateFromClient;
import java.util.HashSet;
import java.util.Iterator;

public class UpdateFromClientMessage {

  private final UpdateFromClient.Builder update = UpdateFromClient.newBuilder();

  public UpdateFromClientMessage(String objectID, PlayerState playerState,
                                 HashSet<GameObjectUUID> deletedCoins, Integer movingRooms) {
    update.setGameObjectUUID(objectID);
    update.setMovingRoomsValue(movingRooms);
    this.constructPlayerMessage(playerState);
    this.constructDeletedCoins(deletedCoins);
  }

  private void constructPlayerMessage(PlayerState playerState) {
    PlayerStateMessage player = new PlayerStateMessage(playerState.color.toString(), playerState.name,
        playerState.score, playerState.x, playerState.y);
    update.setPlayerState(player.getPlayerMessageMessage());
  }

  private void constructDeletedCoins(HashSet<GameObjectUUID> deletedCoins) {
    int i = 0;
    Iterator<GameObjectUUID> coins = deletedCoins.iterator();
    while(coins.hasNext()) {
      update.setDeletedCoins(i, coins.next().toString());
      i++;
    }
  }

  public UpdateFromClient getUpdateFromClientMessage() {
    return this.update.build();
  }

}
