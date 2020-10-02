package edu.rice.comp413fall2020grey.Replication.Messages;

import edu.rice.comp413fall2020grey.Common.GameObject;

import java.util.Date;
import java.util.UUID;

/**
 * Message for subscriptions. The target object is the primary
 * this superpeer would like to subscribe to and recieve updates about.
 */
public class SubscribeMessage extends ReplicaMessage {

  protected SubscribeMessage(Date timestamp, UUID originSuperpeer, UUID targetObject) {
    super(timestamp, originSuperpeer, targetObject);
  }

}
