package edu.rice.rbox.Location.locator;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;
import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Common.ServerUUID;
import edu.rice.rbox.Location.Mongo.MongoManager;
import edu.rice.rbox.Location.interest.InterestPredicate;
import org.bson.*;
import org.bson.conversions.Bson;

import java.util.HashMap;
import java.util.UUID;

public class LocatorMainImpl implements LocatorMain {

    private MongoCollection<Document> mongoCollection;

    private HashMap<GameObjectUUID, InterestPredicate> objectPredicates;

    //TODO: these need to be configured upon initialization of game
    private final String DB_NAME = "trial_db";
    private final String COLLECTION_NAME = "trial_collec";

    //TODO: serverUUID
    private final ServerUUID OUR_SERVER;

    //TODO: real access to methods
    private final Locator2Replication adapter;
    /*
     Init MongoManager and store instance.
     */
    public LocatorMainImpl(ServerUUID serverUUID, Locator2Replication adapter) {
        MongoManager mongoManager = new MongoManager();
        mongoManager.connect();
        this.mongoCollection = mongoManager.getMongoClient().getDatabase(DB_NAME).getCollection(COLLECTION_NAME);

        this.objectPredicates = new HashMap<>();
        this.OUR_SERVER = serverUUID;
        this.adapter = adapter;
    }

    /*
     Call predicate, maybe add condition to not inc. this object_uuid.
     Take results, formulate and send to replication
     maybe return some status code.
     */
    @Override
    public void queryInterest() {

//        Bson bsonUUIDFilter = Filters.ne("_id", new BsonString(object_uuid.toString()));
//        FindIterable<Document> documents = mongoCollection.find(Filters.and(bsonUUIDFilter, bsonQuery));

        // todo: send a message to obj replication
    }

    /*
         Update document in mongo, maybe return some status code.
         Create one if it doesn't exist.
         */
    @Override
    public void addObjectInterest(GameObjectUUID object_uuid, InterestPredicate predicate, HashMap<String, Object> fields) {
        //TODO: error-checking.

        //Save object_uuid and predicate.
        objectPredicates.putIfAbsent(object_uuid, predicate);

        // Create update options where if not exist, we create a new document
        UpdateOptions options = new UpdateOptions();
        options.upsert(true);

        Bson bsonUUID = Filters.eq("_id", new BsonString(object_uuid.getUUID().toString()));
        for (String key: fields.keySet()) {
            //TODO: InterestingGameField.get();
            BasicDBObject updateObject = new BasicDBObject("$set", new BasicDBObject(key, fields.get(key)));
            mongoCollection.updateOne(bsonUUID, updateObject, options);
        }

        mongoCollection.updateOne(bsonUUID, new BasicDBObject(
                "$set",
                new BasicDBObject(
                        "server_uuid",
                        new BsonString(OUR_SERVER.getUUID().toString()
                        )
                )
        ));

        // todo: send a message to obj replication
    }

    /*
     Updates interesting field of given object.
     */
    @Override
    public void updateObjectInterest(GameObjectUUID object_uuid, String fieldName, Object value) {
        Bson bsonUUID = Filters.eq("_id", new BsonString(object_uuid.getUUID().toString()));
        //TODO: InterestingGameField.get();
        BasicDBObject updateObject = new BasicDBObject("$set", new BasicDBObject(fieldName, value));
        mongoCollection.updateOne(bsonUUID, updateObject, new UpdateOptions());
    }

    /*
     Remove document in mongo, maybe return some status code.
     TODO: remove from mongo and remove local predicate.
     */
    @Override
    public void removeObjectInterest(GameObjectUUID object_uuid) {
        Bson bsonUUID = Filters.eq("_id", new BsonString(object_uuid.getUUID().toString()));
        mongoCollection.deleteOne(bsonUUID);

        // todo: send a mesesage to obj replication
    }
}
