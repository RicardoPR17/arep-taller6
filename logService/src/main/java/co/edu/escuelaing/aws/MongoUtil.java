package co.edu.escuelaing.aws;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

public class MongoUtil {
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "taller6";

    public static MongoDatabase getDB() {
        try (MongoClient client = MongoClients.create(CONNECTION_STRING)) {
            return client.getDatabase(DATABASE_NAME);
        }
    }
}
