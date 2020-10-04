package edu.rice.comp413fall2020grey.FaultTolerance.Messages;

import edu.rice.comp413fall2020grey.Common.Message;
import edu.rice.comp413fall2020grey.Common.ServerUUID;

import java.net.InetAddress;
import java.util.Date;

/**
 * A message to notify superpeer servers that a new
 * registrar has been elected.
 */
public class NewRegistrarMessage extends Message {

    /**
     * The address of the newly elected registrar.
     */
    private final InetAddress registrarIP;

    /**
     * Constructor for new registrar alert.
     * @param timestamp Time that this message was sent.
     * @param originSuperpeer Unique ID for the sender of this message.
     * @param registrarIP IP of the new registrar.
     */
    protected NewRegistrarMessage(Date timestamp, ServerUUID originSuperpeer, InetAddress registrarIP) {
        super(timestamp, originSuperpeer);
        this.registrarIP = registrarIP;
    }

    /**
     * @return The IP associated with the new registrar sending this message.
     */
    public InetAddress getRegistrarIP() {
        return this.registrarIP;
    }
}
