package edu.rice.comp413fall2020grey.Location.locator;

import org.bson.conversions.Bson;

public interface LocatorMain {
    /*
     * Method called by the Object Storage to execute new Area-of-Interest.
     */
    void updateInterest(Bson predicate);

}
