package edu.rice.rbox.Location.Mongo;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;


public class MongoManager {

    public static final String DB_NAME = "game_db";
    public static final String COLLECTION_NAME = "game_collection";
    public static final String CLIENT_COLLECTION = "client_collection";
    public static final String SUPERPEER_COLLECTION = "superpeer_collection";

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
