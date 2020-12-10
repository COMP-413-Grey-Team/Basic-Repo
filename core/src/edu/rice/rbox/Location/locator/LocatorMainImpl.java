package edu.rice.rbox.Location.locator;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import edu.rice.rbox.Common.GameField.InterestingGameField;
import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Common.ServerUUID;
import edu.rice.rbox.Game.Server.ObjectStorageKeys;
import edu.rice.rbox.Location.Mongo.MongoManager;
import edu.rice.rbox.Location.interest.InterestPredicate;
import edu.rice.rbox.ObjStorage.ObjectLocationStorageInterface;
import edu.rice.rbox.ObjStorage.ObjectStorageLocationInterface;
import edu.rice.rbox.Replication.HolderInfo;
import edu.rice.rbox.Replication.ObjectLocationReplicationInterface;
import org.bson.*;
import org.bson.conversions.Bson;

import java.util.*;
import java.util.function.Consumer;

public class LocatorMainImpl implements LocatorMain {

    private MongoCollection<Document> mongoCollection;
    private HashMap<GameObjectUUID, InterestPredicate> objectPredicates;
    private final ServerUUID OUR_SERVER;

    //TODO: real access to methods
    private final ObjectLocationStorageInterface storage;
    private final ObjectLocationReplicationInterface replication;
    /*
     Init MongoManager and store instance.
     */
    public LocatorMainImpl(ServerUUID serverUUID,
                           ObjectLocationStorageInterface storage,
                           ObjectLocationReplicationInterface replication) {
         this(serverUUID, storage, replication, MongoManager.DB_NAME, MongoManager.COLLECTION_NAME);
    }

    public LocatorMainImpl(ServerUUID serverUUID,
                           ObjectLocationStorageInterface storage,
                           ObjectLocationReplicationInterface replication,
                           String db_name,
                           String collection_name) {

        MongoManager mongoManager = new MongoManager();
        mongoManager.connect();
        this.mongoCollection = mongoManager.getMongoClient()
                .getDatabase(db_name)
                .getCollection(collection_name);

        this.objectPredicates = new HashMap<>();
        this.OUR_SERVER = serverUUID;
        this.storage = storage;
        this.replication = replication;
    }



    /*
     Call predicate, maybe add condition to not inc. this object_uuid.
     Take results, formulate and send to replication
     maybe return some status code.
     */
    @Override
    public void queryInterest() {

        List<HolderInfo> results = formulate_results();

        replication.handleQueryResult(results);
    }

    /*
     Used for testing
     */
    public List<HolderInfo> formulate_results() {
        List<Bson> allQueries = new ArrayList<>();
        //TODO: replace with HolderInfo;

        Iterator<GameObjectUUID> iterator = this.objectPredicates.keySet().iterator();
        while (iterator.hasNext()) {
            GameObjectUUID gameObjectUUID = iterator.next();
            InterestPredicate interestPredicate = objectPredicates.get(gameObjectUUID); // this line is still wrong
            allQueries.add(interestPredicate.toMongoQuery( gameObjectUUID, storage));
        }


        Optional<Bson> finalQueryOption = allQueries.stream().reduce((q1, q2) -> Filters.or(q1, q2));

        if (finalQueryOption.isEmpty())
            return Collections.emptyList();

        FindIterable<Document> documents = mongoCollection.find(finalQueryOption.get());

        //TODO: extract HolderInfo, formulate and send to replication, want _id and server_uuid

        List<HolderInfo> interesting_objects = new ArrayList();

        documents.forEach((Consumer<? super Document>) doc -> {
            String obj_uuid_string = doc.getString("_id");
            String server_uuid_string = doc.getString("server_uuid");

            GameObjectUUID obj_uuid = new GameObjectUUID(UUID.fromString(obj_uuid_string));
            ServerUUID server_uuid = new ServerUUID(UUID.fromString(server_uuid_string));

            interesting_objects.add(new HolderInfo(obj_uuid, server_uuid));
        });

        return interesting_objects;
    }

    /*
         Update document in mongo, maybe return some status code.
         Create one if it doesn't exist.
         */
    @Override
    public void add(GameObjectUUID gameObjectUUID, InterestPredicate predicate, HashMap<String, InterestingGameField> gameFieldMap) {
        //TODO: error-checking.

        //Save object_uuid and predicate.
        objectPredicates.putIfAbsent(gameObjectUUID, predicate);

        addForTest(gameObjectUUID, gameFieldMap);

    }

    public void addForTest(GameObjectUUID gameObjectUUID, HashMap<String, InterestingGameField> gameFieldMap) {
        // Create update options where if not exist, we create a new document
        UpdateOptions options = new UpdateOptions();
        options.upsert(true);

        Bson bsonUUID = Filters.eq("_id", new BsonString(gameObjectUUID.getUUID().toString()));
        for (String key: gameFieldMap.keySet()) {
            BasicDBObject updateObject = new BasicDBObject("$set", new BasicDBObject(key, gameFieldMap.get(key).getValue()));
            mongoCollection.updateOne(bsonUUID, updateObject, options);
        }

        mongoCollection.updateOne(bsonUUID,
                new BasicDBObject("$set",
                        new BasicDBObject("server_uuid", new BsonString(OUR_SERVER.getUUID().toString()))));

    }

    /*
     Updates interesting field of given object.
     */
    @Override
    public void update(GameObjectUUID gameObjectUUID, String field, InterestingGameField gameField) {
        Bson bsonUUID = Filters.eq("_id", new BsonString(gameObjectUUID.getUUID().toString()));
        BasicDBObject updateObject = new BasicDBObject("$set", new BasicDBObject(field, gameField.getValue()));
        mongoCollection.updateOne(bsonUUID, updateObject, new UpdateOptions());
    }

    /*
     Remove document in mongo, maybe return some status code.
     */
    @Override
    public void delete(GameObjectUUID gameObjectUUID) {
        Bson bsonUUID = Filters.eq("_id", new BsonString(gameObjectUUID.getUUID().toString()));
        mongoCollection.deleteOne(bsonUUID);

        if (objectPredicates.containsKey(gameObjectUUID)) {
            objectPredicates.remove(gameObjectUUID);
        }
    }

    /*
        Adding fields to the global object.
     */

    @Override
    public void addGlobalObjectField(String fieldname, Object fieldvalue) {
        UpdateOptions options = new UpdateOptions();
        options.upsert(true);

        Bson bsonGlobalType = Filters.eq("type", new BsonString(ObjectStorageKeys.Global.TYPE_NAME));

        BasicDBObject updateObject = new BasicDBObject("$set", new BasicDBObject(fieldname, fieldvalue));
        mongoCollection.updateOne(bsonGlobalType, updateObject, options);
    }

    /*
        Retrieving fields from the global object
     */
    @Override
    public Map<String, Object> getGlobalObjectFields() {
        Bson bsonGlobalType = Filters.eq("type", new BsonString("GLOBAL"));

        FindIterable<Document> result = mongoCollection.find(bsonGlobalType);

        Document global_obj_doc = result.first();

        Map<String, Object> global_obj = new HashMap<>();

        global_obj_doc.forEach(
                global_obj::put
        );

        return global_obj;
    }


    /*
     Removes all documents from the connected collection.
     USED FOR TESTING...
     */
    public void removeAllFromCollection() {
        mongoCollection.deleteMany(new BasicDBObject());
    }


}
