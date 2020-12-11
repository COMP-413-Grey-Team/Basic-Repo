package edu.rice.rbox.Game.Client.Messages;

import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Game.Common.SyncState.PlayerState;
import network.GameNetworkProto.UpdateFromClient;
import network.GameNetworkProto.PlayerMessage;

import java.awt.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.stream.Collectors;

public class UpdateFromClientMessage {

  private final UpdateFromClient.Builder update = UpdateFromClient.newBuilder();

  public UpdateFromClientMessage(GameObjectUUID objectID, PlayerState playerState,
                                 HashSet<GameObjectUUID> deletedCoins, Integer movingRooms) {
    update.setGameObjectUUID(objectID.toString());
    update.setMovingRoomsValue(movingRooms);
    this.constructPlayerMessage(playerState);
    update.addAllDeletedCoins(deletedCoins.stream().map(GameObjectUUID::toString).collect(Collectors.toSet()));
  }

  private void constructPlayerMessage(PlayerState playerState) {
    PlayerMessage.Builder player = PlayerMessage.newBuilder();

    update.setPlayerState(player.setColor(Color.BLACK.toString())
                              .setName(playerState.name)
                              .setScore(((Integer) playerState.score).toString())
                              .setX(playerState.x)
                              .setY(playerState.y).build());
  }

  public UpdateFromClient getUpdateFromClientMessage() {
    return this.update.build();
  }

}
