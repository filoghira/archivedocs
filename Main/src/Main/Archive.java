package Main;

import Database.Database;

import java.nio.file.Path;
import java.util.ArrayList;

public class Archive {

    private ArrayList<Document> documents;
    private ArrayList<Tag> tags;
    private Database db;

    public Archive(Database db){
        this.db = db;
    }

    void addDocument(ArrayList<Tag> tags, String name, Path path){

        Document document = new Document(tags, name, path);

        int i=0;
        while(i<tags.size()){
            tags.get(i).addDocument(document);
        }

        // Add the document to the db
    }
}
