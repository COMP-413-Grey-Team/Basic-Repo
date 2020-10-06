package edu.rice.comp413fall2020grey.Replication.Messages;

import edu.rice.comp413fall2020grey.Common.GameObject;
import edu.rice.comp413fall2020grey.Common.GameObjectUUID;
import edu.rice.comp413fall2020grey.Common.ServerUUID;

import java.util.Date;

/**
 * Message for subscriptions. The target object is the primary
 * this superpeer would like to subscribe to and receive updates about.
 */
public class SubscribeMessage extends ReplicaMessage {

  /**
   * Constructor for update message.
   * @param timestamp The time this message was sent.
   * @param originSuperpeer Unique ID for message sender.
   * @param targetObject Unique ID for object originSuperpeer would like to subscribe to.
   */
  protected SubscribeMessage(Date timestamp, ServerUUID originSuperpeer, GameObjectUUID targetObject) {
    super(timestamp, originSuperpeer, targetObject, "subscribe");
  }

}
