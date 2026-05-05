package cms;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MongoStore {

    private static final String MONGO_URI = System.getenv().getOrDefault("CMS_MONGO_URI", "mongodb://localhost:27017");
    private static final String DATABASE_NAME = System.getenv().getOrDefault("CMS_MONGO_DB", "cms");

    private static final boolean available;
    private static MongoClient client;
    private static MongoDatabase database;

    static {
        boolean ok = false;
        try {
            client = MongoClients.create(MONGO_URI);
            database = client.getDatabase(DATABASE_NAME);
            // Verify the server is reachable before enabling MongoDB mode.
            database.listCollectionNames().first();
            ok = true;
        } catch (Exception e) {
            System.err.println("[MongoStore] MongoDB initialization failed: " + e.getMessage());
            if (client != null) {
                try { client.close(); } catch (Exception ignored) {}
            }
        }
        available = ok;
    }

    public static boolean isAvailable() {
        return available;
    }

    public static MongoCollection<Document> collection(String name) {
        if (!available) throw new IllegalStateException("MongoDB is not available");
        return database.getCollection(name);
    }

    public static List<Document> readAll(String collectionName) {
        List<Document> docs = new ArrayList<>();
        if (!available) return docs;
        FindIterable<Document> iterable = collection(collectionName).find();
        for (Document doc : iterable) {
            docs.add(doc);
        }
        return docs;
    }

    public static void replaceCollection(String collectionName, List<Document> docs) {
        if (!available) return;
        MongoCollection<Document> collection = collection(collectionName);
        collection.deleteMany(new Document());
        if (!docs.isEmpty()) {
            collection.insertMany(docs);
        }
    }

    public static void upsert(String collectionName, Document doc, String keyField) {
        if (!available) return;
        collection(collectionName).replaceOne(
            Filters.eq(keyField, doc.getString(keyField)),
            doc,
            new ReplaceOptions().upsert(true)
        );
    }
}
