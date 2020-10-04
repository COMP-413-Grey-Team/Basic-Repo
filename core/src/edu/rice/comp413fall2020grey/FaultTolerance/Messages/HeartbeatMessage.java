package edu.rice.comp413fall2020grey.FaultTolerance.Messages;

import edu.rice.comp413fall2020grey.Common.Message;
import edu.rice.comp413fall2020grey.Common.ServerUUID;

import java.util.Date;

/**
 * Heartbeat message sent from the registrar to the superpeers.
 */
public class HeartbeatMessage extends Message {

    /**
     * Constructor for heartbeat messages.
     * @param timestamp Time this message was sent.
     * @param originSuperpeer Unique ID for message sender.
     */
    protected HeartbeatMessage(Date timestamp, ServerUUID originSuperpeer) {
        super(timestamp, originSuperpeer);
    }
}
