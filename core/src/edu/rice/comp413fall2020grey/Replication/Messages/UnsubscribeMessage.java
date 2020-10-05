package edu.rice.comp413fall2020grey.Replication.Messages;

import edu.rice.comp413fall2020grey.Common.GameObjectUUID;
import edu.rice.comp413fall2020grey.Common.ServerUUID;

import java.util.Date;

/**
 * Message for unsubscribing. The target object is the primary
 * this superpeer no longer requires updates about.
 */
public class UnsubscribeMessage extends ReplicaMessage {

    /**
     * Constructor for unsubscribe message.
     * @param timestamp The time this message was sent.
     * @param originSuperpeer Unique ID for the sender of this message.
     * @param targetObject The primary this superpeer no longer requires updates about.
     */
    protected UnsubscribeMessage(Date timestamp, ServerUUID originSuperpeer, GameObjectUUID targetObject) {
        super(timestamp, originSuperpeer, targetObject);
    }
}
