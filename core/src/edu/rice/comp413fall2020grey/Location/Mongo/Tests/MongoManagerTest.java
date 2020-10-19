package edu.rice.comp413fall2020grey.Location.Mongo.Tests;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import edu.rice.comp413fall2020grey.Location.Mongo.MongoManager;
import org.bson.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import java.util.ArrayList;

import static com.mongodb.client.model.Filters.eq;

/**
 * Make sure to add -Dtestng.dtd.http=true in intelliJ vm options for this file.
 */
public class MongoManagerTest {

    private static MongoManager mongoManager;
    private static String password = "UwBNkGQwtdyRBbNf";

    @BeforeAll
    public static void setupDBConnection() {
        System.out.println("do here");
        mongoManager = new MongoManager(password);
        mongoManager.connect();
    }

    @Test
    public void testTrialClusterConnection() {
        String database = "sample_airbnb";
        MongoClient client = mongoManager.getMongoClient();
        String collectionName = client.getDatabase(database).listCollectionNames().first();
        Assertions.assertEquals(collectionName, "listingsAndReviews");
    }

    @Test
    public void testTrialClusterConnection2() {
        String database = "sample_analytics";
        MongoClient client = mongoManager.getMongoClient();

        ArrayList<String> collectionNames  = client.getDatabase(database)
                                                 .listCollectionNames()
                                                 .into(new ArrayList<String>());

        Assertions.assertTrue(collectionNames.contains("accounts"));
        Assertions.assertTrue(collectionNames.contains("customers"));
        Assertions.assertTrue(collectionNames.contains("transactions"));
    }

    @Test
    public void testTrialClusterQuery() {
        String database = "sample_airbnb";
        MongoClient client = mongoManager.getMongoClient();
        FindIterable<Document> documents =
            client.getDatabase(database).getCollection("listingsAndReviews").find(eq("_id", "10009999"));
        Assertions.assertNotNull(documents.first());
        System.out.println(documents.first());
    }
}
