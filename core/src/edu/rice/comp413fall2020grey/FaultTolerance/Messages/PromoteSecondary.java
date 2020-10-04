package edu.rice.comp413fall2020grey.FaultTolerance.Messages;

import edu.rice.comp413fall2020grey.Common.Message;

import java.util.Date;
import java.util.UUID;

/**
 * Message informing a superpeer they have a new primary object in
 * their possession.
 */
public class PromoteSecondary extends Message {

    /**
     * The UUID of the old primary being replaced.
     */
    private final UUID oldPrimary;

    /**
     * The UUID of the secondary replacing the old primary.
     */
    private final UUID replacement;

    /**
     * Constructor for promotion message.
     * @param timestamp Time when this message was sent.
     * @param originSuperpeer Unique ID for the sender of this message.
     * @param oldPrimary Unique ID for primary being replaced
     * @param replacement Unique ID for the replacement
     */
    protected PromoteSecondary(Date timestamp, UUID originSuperpeer, UUID oldPrimary, UUID replacement) {
        super(timestamp, originSuperpeer);
        this.oldPrimary = oldPrimary;
        this.replacement = replacement;
    }

    /**
     * @return UUID of the old primary being replaced.
     */
    public UUID getOldPrimary() {
        return this.oldPrimary;
    }

    /**
     * @return UUID of the secondary replacing the old primary.
     */
    public UUID getReplacement() {
        return this.replacement;
    }
}
