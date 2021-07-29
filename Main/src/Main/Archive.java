package Main;

import Database.Database;

import java.nio.file.Path;
import java.util.ArrayList;

import static Database.Database.mainTable;
import static Database.Database.mainTableColumns;

public class Archive {

    private ArrayList<Document> documents;
    private ArrayList<Tag> tags;
    private Database db;

    public Archive(String username, String password){
        db = new Database(username, password);
    }

    public Archive(String username){
        db = new Database(username, "");
    }

    void addDocument(ArrayList<Tag> tags, String name, Path path){

        Document document = new Document(tags, name, path);

        int i=0;
        while(i<tags.size()){
            tags.get(i).addDocument(document);
        }

        String[][] data = new String[mainTableColumns.length-1][2];

        i=1;
        while(i< mainTableColumns.length){
            data[i][0] = mainTableColumns[i][0];
            data[i][1] = document.getProp(mainTableColumns[i][1]);
        }

        // Add the document to the db
        db.addRow(mainTable, data);
    }
}
