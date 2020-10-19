package edu.rice.comp413fall2020grey.Location.Mongo;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;


public class MongoManager {

    private final String PASSWORD;
    private MongoClient mongoClient;

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public MongoManager(String password) {
        PASSWORD = password;
    }

    public void connect() {
        mongoClient = MongoClients.create(
            "mongodb+srv://object-locator:" + PASSWORD +
                "@rboxtrial.kfli3.mongodb.net");
    }

}
