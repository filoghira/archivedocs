package Main;

import Database.Database;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static Database.Database.*;

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

    public void addDocument(ArrayList<Tag> tags, String name, Path path){

        Document document = new Document(tags, name, path);

        int i=0;
        if(tags != null)
            while (i < tags.size()) {
                tags.get(i).addDocument(document);
                i++;
            }

        String[][] data = new String[mainTableColumns.length][2];

        i=0;
        while(i< mainTableColumns.length){
            data[i][0] = mainTableColumns[i][0];
            data[i][1] = document.getProp(mainTableColumns[i][0]);
            i++;
        }

        // Add the document to the db
        db.addRow(mainTable, data);
    }

    public void addDocument(String name, Path path){
        addDocument(null, name, path);
    }

    public void addTag(ArrayList<Document> documents, String name){
        Tag tag = new Tag(documents, name);

        int i=0;
        if(documents != null)
            while (i < documents.size()) {
                documents.get(i).addTag(tag);
                i++;
            }

        String[][] data = new String[tagsTableColumns.length][2];

        i=0;
        while(i< tagsTableColumns.length){
            data[i][0] = tagsTableColumns[i][0];
            data[i][1] = tag.getProp(tagsTableColumns[i][0]);
            i++;
        }

        // Add the document to the db
        db.addRow(tagsTable, data);

        // Create the table of the tag
        db.addTable(name, tagColumns);
    }

    public void addTag(String name){
        addTag(null, name);
    }

    void updateFromDB(){

        // SELECT every document from the main table in the database
        ResultSet rs = db.getAllFromTable(mainTable);

        try {

            ArrayList<Document> documents = new ArrayList<>();

            // Go through result set and create documents
            while (rs.next()) {
                String fileName = rs.getString(1);
                String filePath = rs.getString(2);

                documents.add(new Document(null, fileName, Paths.get(filePath)));
            }

            // Update the document list
            this.documents = documents;

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
