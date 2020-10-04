package edu.rice.comp413fall2020grey.FaultTolerance.Messages;

import edu.rice.comp413fall2020grey.Common.Message;
import edu.rice.comp413fall2020grey.Common.ServerUUID;

import java.util.Date;

/**
 * Message informing a superpeer they have a new primary object in
 * their possession.
 */
public class PromoteSecondaryMessage extends Message {

    /**
     * The UUID of the old primary being replaced.
     */
    private final ServerUUID oldPrimary;

    /**
     * The UUID of the secondary replacing the old primary.
     */
    private final ServerUUID replacement;

    /**
     * Constructor for promotion message.
     * @param timestamp Time when this message was sent.
     * @param originSuperpeer Unique ID for the sender of this message.
     * @param oldPrimary Unique ID for primary being replaced
     * @param replacement Unique ID for the replacement
     */
    protected PromoteSecondaryMessage(Date timestamp, ServerUUID originSuperpeer, ServerUUID oldPrimary, ServerUUID replacement) {
        super(timestamp, originSuperpeer);
        this.oldPrimary = oldPrimary;
        this.replacement = replacement;
    }

    /**
     * @return UUID of the old primary being replaced.
     */
    public ServerUUID getOldPrimary() {
        return this.oldPrimary;
    }

    /**
     * @return UUID of the secondary replacing the old primary.
     */
    public ServerUUID getReplacement() {
        return this.replacement;
    }
}
