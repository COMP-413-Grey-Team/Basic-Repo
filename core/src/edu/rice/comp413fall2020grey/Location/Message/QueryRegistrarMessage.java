package edu.rice.comp413fall2020grey.Location.Message;

import edu.rice.comp413fall2020grey.Common.Message;
import edu.rice.comp413fall2020grey.Common.ServerUUID;
import edu.rice.comp413fall2020grey.Location.interest.InterestPredicate;

import java.util.Date;
import java.util.function.Predicate;

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