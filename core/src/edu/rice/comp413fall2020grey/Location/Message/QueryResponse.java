package edu.rice.comp413fall2020grey.Location.Message;

import edu.rice.comp413fall2020grey.Common.Message;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Message for responding to query requests.
 * Sent from the registrar to the superpeers.
 */
public class QueryResponse extends Message {

    /**
     * A set of mappings for obtaining new
     * objects of interest. Maps are organized as follows:
     *
     *      GameObject UUID -> Corresponding server UUID
     */
    private final Set<Map<UUID, UUID>> objectsOfInterest;

    /**
     * Constructor for query responses.
     * @param timestamp The time this message was sent.
     * @param originSuperpeer Unique ID of message sender.
     * @param objectsOfInterest Set of objects of interest for the superpeer to obtain.
     */
    protected QueryResponse(Date timestamp, UUID originSuperpeer, Set<Map<UUID, UUID>> objectsOfInterest) {
        super(timestamp, originSuperpeer);
        this.objectsOfInterest = objectsOfInterest;
    }

    /**
     * @return The associated objects of interest for the query response.
     */
    public Set<Map<UUID, UUID>> getObjectsOfInterest() { return objectsOfInterest; }
}
