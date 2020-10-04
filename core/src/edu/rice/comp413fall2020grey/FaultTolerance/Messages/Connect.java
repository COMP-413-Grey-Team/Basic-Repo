package edu.rice.comp413fall2020grey.FaultTolerance.Messages;

import edu.rice.comp413fall2020grey.Common.Message;

import java.net.InetAddress;
import java.util.Date;
import java.util.UUID;

/**
 * Message ordering the superpeer to connect to the associated client.
 */
public class Connect extends Message {

    /**
     * The address of the client requiring a new connection.
     */
    private final InetAddress clientAddress;

    /**
     * Constructor for connection message.
     * @param timestamp The time this message was sent.
     * @param originSuperpeer Unique ID for the sender of this message.
     * @param clientAddress IP Address of the client requiring connection.
     */
    protected Connect(Date timestamp, UUID originSuperpeer, InetAddress clientAddress) {
        super(timestamp, originSuperpeer);
        this.clientAddress = clientAddress;
    }

    /**
     * @return The IP address of the client requiring a new connection.
     */
    public InetAddress getClientAddress() {
        return clientAddress;
    }
}
