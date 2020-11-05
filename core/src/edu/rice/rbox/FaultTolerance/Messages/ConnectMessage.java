package edu.rice.rbox.FaultTolerance.Messages;

import edu.rice.rbox.Common.Message;
import edu.rice.rbox.Common.ServerUUID;

import java.net.InetAddress;
import java.util.Date;

/**
 * Message ordering the superpeer to connect to the associated client.
 */
public class ConnectMessage extends Message {

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
    protected ConnectMessage(Date timestamp, ServerUUID originSuperpeer, InetAddress clientAddress) {
        super(timestamp, originSuperpeer, "connect");
        this.clientAddress = clientAddress;
    }

    /**
     * @return The IP address of the client requiring a new connection.
     */
    public InetAddress getClientAddress() {
        return clientAddress;
    }
}
