package edu.rice.rbox.Game.Common.Utils;

import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Game.Common.SyncState.PlayerState;
import network.GameNetworkProto.UpdateFromClient;
import network.GameNetworkProto.PlayerMessage;
import java.util.HashSet;
import java.util.Iterator;

public class UpdateFromClientMessage {

  private final UpdateFromClient.Builder update = UpdateFromClient.newBuilder();

  public UpdateFromClientMessage(GameObjectUUID objectID, PlayerState playerState,
                                 HashSet<GameObjectUUID> deletedCoins, Integer movingRooms) {
    update.setGameObjectUUID(objectID.toString());
    update.setMovingRoomsValue(movingRooms);
    this.constructPlayerMessage(playerState);
    this.constructDeletedCoins(deletedCoins);
  }

  private void constructPlayerMessage(PlayerState playerState) {
    PlayerMessage.Builder player = PlayerMessage.newBuilder();

    update.setPlayerState(player.setColor(playerState.color.toString())
                              .setName(playerState.name)
                              .setScore(((Integer) playerState.score).toString())
                              .setX(playerState.x)
                              .setY(playerState.y).build());
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
