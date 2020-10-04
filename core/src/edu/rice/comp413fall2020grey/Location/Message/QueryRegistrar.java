package edu.rice.comp413fall2020grey.Location.Message;

import edu.rice.comp413fall2020grey.Common.GameObjectMetadata;
import edu.rice.comp413fall2020grey.Common.Message;

import java.util.Date;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Message for querying the registrar for objects
 * of interest.
 */
public class QueryRegistrar extends Message {

    /**
     * Interest criteria of the object querying the registrar.
     */
    private final Predicate<GameObjectMetadata> interestCriteria;

    /**
     * Constructor for a query message.
     * @param timestamp Time this message was sent.
     * @param originSuperpeer Unique ID of the message sender.
     * @param interestCriteria Interest criteria for object querying registrar.
     */
    protected QueryRegistrar(Date timestamp, UUID originSuperpeer, Predicate<GameObjectMetadata> interestCriteria) {
        super(timestamp, originSuperpeer);
        this.interestCriteria = interestCriteria;
    }

    /**
     * @return The interest criteria associated with this message.
     */
    public Predicate<GameObjectMetadata> getInterestCriteria() { return interestCriteria; }
}
