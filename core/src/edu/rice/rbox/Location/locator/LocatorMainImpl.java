package edu.rice.comp413fall2020grey.Location.locator;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;
import edu.rice.comp413fall2020grey.Location.Mongo.MongoManager;
import org.bson.*;
import org.bson.conversions.Bson;

import java.util.HashMap;
import java.util.UUID;

public class LocatorMainImpl implements LocatorMain {

    private MongoCollection<Document> mongoCollection;

    //TODO: these need to be configured upon initialization of game
    private final String DB_NAME = "trial_db";
    private final String COLLECTION_NAME = "trial_collec";

    /*
     Init MongoManager and store instance.
     */
    public LocatorMainImpl() {
        MongoManager mongoManager = new MongoManager();
        mongoManager.connect();
        mongoCollection = mongoManager.getMongoClient().getDatabase(DB_NAME).getCollection(COLLECTION_NAME);
    }

    /*
     Call predicate, maybe add condition to not inc. this object_uuid.
     Take results, formulate and send to replication
     maybe return some status code.
     */
    @Override
    public void queryInterest(UUID object_uuid, Bson bsonQuery) {

        Bson bsonUUIDFilter = Filters.ne("_id", new BsonString(object_uuid.toString()));
        FindIterable<Document> documents = mongoCollection.find(Filters.and(bsonUUIDFilter, bsonQuery));

        // todo: send a message to obj replication
    }

    /*
     Update document in mongo, maybe return some status code.
     Create one if it doesn't exist.
     */
    @Override
    public void updatePrimary(UUID object_uuid, HashMap<String, Object> fields) {
        // Create update options where if not exist, we create a new document
        UpdateOptions options = new UpdateOptions();
        options.upsert(true);

        Bson bsonUUID = Filters.eq("_id", new BsonString(object_uuid.toString()));
        for (String key: fields.keySet()) {
            BasicDBObject updateObject = new BasicDBObject("$set", new BasicDBObject(key, fields.get(key)));
            mongoCollection.updateOne(bsonUUID, updateObject, options);
        }

        // todo: send a message to obj replication
    }

    /*
     Remove document in mongo, maybe return some status code.
     */
    @Override
    public void removePrimary(UUID object_uuid) {
        Bson bsonUUID = Filters.eq("_id", new BsonString(object_uuid.toString()));
        mongoCollection.deleteOne(bsonUUID);

        // todo: send a mesesage to obj replication
    }
}
