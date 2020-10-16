package edu.rice.comp413fall2020grey.Location.Mongo;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;


public class MongoManager {

    private static final String PASSWORD = "UwBNkGQwtdyRBbNf";

    public static void main(String[] args) {
        MongoManager.connectToDB();
    }

    public static void connectToDB() {

        System.out.println("About to connect");
        MongoClient mongoClient = MongoClients.create(
            "mongodb+srv://object-locator:" + PASSWORD +
                "@rboxtrial.kfli3.mongodb.net/sample_airbnb?retryWrites=true&w=majority");

        System.out.println("Connected...");
        MongoDatabase database = mongoClient.getDatabase("test");

        System.out.println("Getting Database");
        ArrayList<String> dbList = database.listCollectionNames().into(new ArrayList<String>());
        for (String col: dbList) {
            System.out.println(col);
        }
    }

}
