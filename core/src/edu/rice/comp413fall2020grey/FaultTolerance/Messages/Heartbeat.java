package edu.rice.comp413fall2020grey.FaultTolerance.Messages;

import edu.rice.comp413fall2020grey.Common.Message;

import java.util.Date;
import java.util.UUID;

/**
 * Heartbeat message sent from the registrar to the superpeers.
 */
public class Heartbeat extends Message {

    /**
     * Constructor for heartbeat messages.
     * @param timestamp Time this message was sent.
     * @param originSuperpeer Unique ID for message sender.
     */
    protected Heartbeat(Date timestamp, UUID originSuperpeer) {
        super(timestamp, originSuperpeer);
    }
}
