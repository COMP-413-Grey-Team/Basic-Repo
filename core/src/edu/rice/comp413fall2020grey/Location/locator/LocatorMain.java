package edu.rice.comp413fall2020grey.Location.locator;

import org.bson.conversions.Bson;

import java.util.HashMap;
import java.util.UUID;

public interface LocatorMain {

    /* Fetching objects of interest to this object given a predicate. */
    void queryInterest(UUID object_uuid, Bson predicate);
    /* Adding/Updating the interesting fields for this object. */
    void updatePrimary(UUID object_uuid, HashMap<String, Object> fields);
    /* Removing the interesting fields for this object */
    void removePrimary(UUID object_uuid);

}
