package Main;

import Database.Database;
import GhiraUtils.General;
import GhiraUtils.SQLUtils;

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
        tags = new ArrayList<>();
        updateTagsFromDB();
    }

    public Archive(String username){
        db = new Database(username, "");
    }

    private ArrayList<Tag> parseTags(String[] tags){
        ArrayList<Tag> out = new ArrayList<>();

        int i=0;
        while(i<tags[i].length()){
            int k=0;
            while(k<this.tags.size()){
                if(
                        tags[i].equals(
                                this.tags.get(i).getProp(
                                        tagsTableColumns[0][0])
                        )
                )
                    out.add(this.tags.get(i));

                k++;
            }
            i++;
        }

        return out;
    }

    public void addDocument(String[] tagStrings, String name, Path path){

        ArrayList<Tag> tags = parseTags(tagStrings);

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
        ResultSet rs = db.addRow(mainTable, data);
        try {
            int id = rs.getInt(1);
            document.setID(id);
        } catch (SQLException e) {
            SQLUtils.printSQLException(e);
        }

        for(i=0; i<tags.size(); i++)
            db.addRow(tags.get(i).getProp(tagsTableColumns[0][0]), new String[][] {{tagColumns[0][0], Integer.toString(document.getID())}});
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

    void updateDocumentsFromDB(){

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

    void updateTagsFromDB(){

        ResultSet rs = db.getAllFromTable(tagsTable);

        try {

            ArrayList<Tag> tags = new ArrayList<>();

            // Go through result set and create documents
            while (rs.next()) {
                String tagName = rs.getString(1);

                tags.add(new Tag(null, tagName));
            }

            // Update the document list
            this.tags = tags;

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
