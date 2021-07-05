package model.db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import presentation.Presentation;

import java.util.ArrayList;
import java.util.List;

public class DBManager<T> {

    private final static String ID_FIELD = "gameID";

    private final MongoDatabase db;
    private final MongoCollection<Document> collection;
    private final Class<T> klass;

    public DBManager(String dbName, String collectionName, Class<T> klass) {
        MongoClient client = MongoClients.create(System.getenv("MONGODB"));
        db = client.getDatabase(dbName);
        collection = db.getCollection(collectionName);
        this.klass = klass;
    }

    public boolean isPresent(String id){
        return collection.countDocuments(Filters.eq(ID_FIELD, id)) == 0;
    }

    public void insert(T elem){
        collection.insertOne(convertToDocument(elem));
    }

    public void update(String id, T elem){
        collection.replaceOne(Filters.eq(ID_FIELD, id),convertToDocument(elem));
    }

    public void remove(String id){
        collection.deleteOne(Filters.eq(ID_FIELD, id));
    }

    public List<T> getAllElement(){
        List<T> elements = new ArrayList<>();
        collection.find().forEach(doc -> {
            try {
                elements.add(Presentation.deserializeAs(doc.toJson(), klass));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return elements;
    }

    private Document convertToDocument(T elem){
        return Document.parse(Presentation.serializerOf(klass).serialize(elem));
    }


}
