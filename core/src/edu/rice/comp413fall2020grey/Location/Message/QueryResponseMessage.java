package edu.rice.comp413fall2020grey.Location.Message;

import edu.rice.comp413fall2020grey.Common.GameObjectUUID;
import edu.rice.comp413fall2020grey.Common.Message;
import edu.rice.comp413fall2020grey.Common.ServerUUID;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Message for responding to query requests.
 * Sent from the registrar to the superpeers.
 */
public class QueryResponseMessage extends Message {

    /**
     * A set of mappings for obtaining new
     * objects of interest. Maps are organized as follows:
     *
     *      GameObject UUID -> Corresponding server UUID
     */
    private final Set<Map<GameObjectUUID, ServerUUID>> objectsOfInterest;

    /**
     * Constructor for query responses.
     * @param timestamp The time this message was sent.
     * @param originSuperpeer Unique ID of message sender.
     * @param objectsOfInterest Set of objects of interest for the superpeer to obtain.
     */
    protected QueryResponseMessage(Date timestamp, ServerUUID originSuperpeer, Set<Map<GameObjectUUID, ServerUUID>> objectsOfInterest) {
        super(timestamp, originSuperpeer, "query-response");
        this.objectsOfInterest = objectsOfInterest;
    }

    /**
     * @return The associated objects of interest for the query response.
     */
    public Set<Map<GameObjectUUID, ServerUUID>> getObjectsOfInterest() { return objectsOfInterest; }
}
