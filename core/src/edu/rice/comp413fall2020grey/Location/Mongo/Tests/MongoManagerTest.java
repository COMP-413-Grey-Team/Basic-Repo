package edu.rice.comp413fall2020grey.Location.Mongo.Tests;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import edu.rice.comp413fall2020grey.Location.Mongo.MongoManager;
import edu.rice.comp413fall2020grey.Location.interest.CompositePredicate;
import edu.rice.comp413fall2020grey.Location.interest.EqualityNumberPredicate;
import edu.rice.comp413fall2020grey.Location.interest.EqualityStringPredicate;
import edu.rice.comp413fall2020grey.Location.interest.PredicateBiOperator;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

/**
 * Make sure to add -Dtestng.dtd.http=true in intelliJ vm options for this file.
 */
public class MongoManagerTest {

    private static MongoManager mongoManager;
    private static MongoClient mongoClient;
    private static String password = "UwBNkGQwtdyRBbNf";
    private final String AIRBNB_DB = "sample_airbnb";
    private final String ANALYTICS_DB = "sample_analytics";

    @BeforeAll
    public static void setupDBConnection() {
        mongoManager = new MongoManager(password);
        mongoManager.connect();
        mongoClient = mongoManager.getMongoClient();
    }

    @Test
    public void testTrialClusterConnection() {
        String database = "sample_airbnb";
        String collectionName = mongoClient.getDatabase(AIRBNB_DB).listCollectionNames().first();
        Assertions.assertEquals(collectionName, "listingsAndReviews");
    }

    @Test
    public void testTrialClusterConnection2() {
        ArrayList<String> collectionNames  = mongoClient.getDatabase(ANALYTICS_DB)
                                                 .listCollectionNames()
                                                 .into(new ArrayList<String>());

        Assertions.assertTrue(collectionNames.contains("accounts"));
        Assertions.assertTrue(collectionNames.contains("customers"));
        Assertions.assertTrue(collectionNames.contains("transactions"));
    }

    @Test
    public void testTrialClusterQuery() {
        FindIterable<Document> documents =
            mongoClient.getDatabase(AIRBNB_DB).getCollection("listingsAndReviews").find(eq("_id", "10009999"));
        Assertions.assertNotNull(documents.first());
        System.out.println(documents.first());
    }

    @Test
    public void testEqualityPredicateQuery() {

        MongoCollection<Document> collection =
            mongoClient.getDatabase(AIRBNB_DB).getCollection("listingsAndReviews");

        EqualityStringPredicate equalityPred = new EqualityStringPredicate("_id", "10009999");
        Bson bsonQuery = equalityPred.toMongoQuery(null);
        FindIterable<Document> expected = collection.find(eq("_id", "10009999"));
        FindIterable<Document> actual  = collection.find(bsonQuery);
        Assertions.assertEquals(expected.first(), actual.first());
        System.out.println(actual.first());
    }

    @Test
    public void testCompositePredicateQuery() {
        MongoCollection<Document> collection =
            mongoClient.getDatabase(AIRBNB_DB).getCollection("listingsAndReviews");

        CompositePredicate compositePred = new CompositePredicate(
            new EqualityNumberPredicate("beds", 5, false),
            new EqualityNumberPredicate("bathrooms", 2, false),
            PredicateBiOperator.AND);

        Bson bsonQuery = compositePred.toMongoQuery(null);
        FindIterable<Document> expected = collection.find(and(eq("beds", 5), eq("bathrooms", 2)));
        FindIterable<Document> actual  = collection.find(bsonQuery);

        Assertions.assertEquals(expected.first(), actual.first());
        System.out.println(actual.first());
    }

    @Test
    public void testEqualityPredicateQueryBack() {

        MongoCollection<Document> collection =
            mongoClient.getDatabase(AIRBNB_DB).getCollection("listingsAndReviews");

        EqualityStringPredicate equalityPred = new EqualityStringPredicate("_id", "10009999");

        String toJson = equalityPred
                    .toMongoQuery(null)
                    .toBsonDocument(null,null) // todo: what is our codecRegistry
                    .toJson();

        Bson backToBson = BsonDocument.parse(toJson);
        System.out.println(collection.find(backToBson).first());
    }

}
