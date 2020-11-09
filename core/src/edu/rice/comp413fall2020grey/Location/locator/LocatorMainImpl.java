package edu.rice.comp413fall2020grey.Location.locator;

import org.bson.conversions.Bson;

import java.util.HashMap;
import java.util.UUID;

public class LocatorMainImpl implements LocatorMain {

    /*
     Init MongoManager and store instance.
     */
    public LocatorMainImpl() {

    }

    /*
     Call predicate, maybe add condition to not inc. this object_uuid.
     Take results, formulate and send to replication
     maybe return some status code.
     */
    @Override
    public void queryInterest(UUID object_uuid, Bson predicate) {

    }

    /*
     Update document in mongo, maybe return some status code.
     */
    @Override
    public void updatePrimary(UUID object_uuid, HashMap<String, Object> fields) {

    }

    /*
     Remove document in mongo, maybe return some status code.
     */
    @Override
    public void removePrimary(UUID object_uuid) {

    }
}
