package edu.rice.rbox.Location.Mongo;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;


public class MongoManager {

    private final String PASSWORD = "UwBNkGQwtdyRBbNf";
    private MongoClient mongoClient;

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public MongoManager() {
    }

    public void connect() {
        mongoClient = MongoClients.create(
            "mongodb+srv://object-locator:" + PASSWORD +
                "@rboxtrial.kfli3.mongodb.net");
    }

}
