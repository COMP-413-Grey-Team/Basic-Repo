package edu.rice.comp413fall2020grey.Location.locator;

import com.mongodb.client.MongoClient;
import edu.rice.comp413fall2020grey.Location.Mongo.MongoManager;
import org.bson.conversions.Bson;

import java.util.HashMap;
import java.util.UUID;

public class LocatorMainImpl implements LocatorMain {
    private final MongoClient mongoClient;

    /*
     Init MongoManager and store instance.
     */
    public LocatorMainImpl() {
        MongoManager mongoManager = new MongoManager();
        mongoManager.connect();
        mongoClient = mongoManager.getMongoClient();
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
