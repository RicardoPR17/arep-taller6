package co.edu.escuelaing.aws;

import static spark.Spark.get;
import static spark.Spark.port;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;

import static com.mongodb.client.model.Sorts.*;

public class LogService {
    public static void main(String... args) {
        port(getPort());
        get("/logfacade", (req, res) -> {
            String msg = req.queryParams("message");
            return getRecords(msg);
        });
    }

    /**
     * Save the new document in the database and return the 10 most recent documents
     * in the collection
     * 
     * @param newMsg The message to save in the database
     * @return A string representation of the JSON with the 10 most recent documents
     */
    private static String getRecords(String newMsg) {
        MongoDatabase database = MongoUtil.getDB();
        MongoCollection<Document> messages = database.getCollection("logs");

        // Save the new document in the database
        Document newDoc = new Document()
                .append("message", newMsg)
                .append("date", new Date());
        messages.insertOne(newDoc);

        // Get the 10 most recent documents
        List<Document> results = new ArrayList<>();
        Bson projection = Projections.fields(Projections.include("date", "message"), Projections.excludeId());
        messages.find().sort(descending("date")).projection(projection).limit(10).into(results);

        // Prepare Gson response
        Gson json = new Gson();
        return json.toJson(results);
    }

    /**
     * Give the port for the server
     * 
     * @return If the PORT enviroment variable is define, return his value.
     *         Otherwise, 5000
     */
    private static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 5000;
    }
}
