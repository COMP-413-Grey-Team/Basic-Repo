package edu.rice.rbox.Location.Message;

import edu.rice.rbox.Common.Message;
import edu.rice.rbox.Common.ServerUUID;
import edu.rice.rbox.Location.interest.InterestPredicate;

import java.util.Date;

/**
 * Message for querying the registrar for objects
 * of interest.
 */
public class QueryRegistrarMessage extends Message {

    /**
     * Interest criteria of the object querying the registrar.
     */
    private final InterestPredicate interestCriteria;

    /**
     * Constructor for a query message.
     * @param timestamp Time this message was sent.
     * @param originSuperpeer Unique ID of the message sender.
     * @param interestCriteria Interest criteria for object querying registrar.
     */
    protected QueryRegistrarMessage(Date timestamp, ServerUUID originSuperpeer, InterestPredicate interestCriteria) {
        super(timestamp, originSuperpeer, "query-registrar");
        this.interestCriteria = interestCriteria;
    }

    /**
     * @return The interest criteria associated with this message.
     */
    public InterestPredicate getInterestCriteria() { return interestCriteria; }
}