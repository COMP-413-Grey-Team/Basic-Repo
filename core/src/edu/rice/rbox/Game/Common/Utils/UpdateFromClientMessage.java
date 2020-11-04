package edu.rice.rbox.Game.Common.Utils;

import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Game.Common.SyncState.PlayerState;
import edu.rice.rbox.Protos.Generated.GameNetworkProto.UpdateFromClient;
import java.util.HashSet;

public class UpdateFromClientMessage {

  private final UpdateFromClient.Builder update = UpdateFromClient.newBuilder();

  public UpdateFromClientMessage(String objectID, PlayerState playerState,
                                 HashSet<GameObjectUUID> deletedCoins, Integer movingRooms) {
    update.setGameObjectUUID(objectID);
    update.setMovingRoomsValue(movingRooms);
    this.constructPlayerState(playerState);
    this.constructDeletedCoins(deletedCoins);
  }

  private void constructPlayerState(PlayerState playerState) {

  }

  private void constructDeletedCoins(HashSet<GameObjectUUID> deletedCoins) {

  }

  public UpdateFromClient getUpdateFromClientMessage() {
    return this.update.build();
  }

}
