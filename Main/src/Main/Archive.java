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
    private Node tagTree = new Node(null);
    private Database db;

    public Archive(String username, String password){
        db = new Database(username, password);
        updateTagsFromDB();
    }

    public Archive(String username){
        db = new Database(username, "");
    }

    /**
     * Converts an array of strings containing the name of the tags into a list of Tags
     * @param tags An array of strings containing the names of the tags
     * @return An ArrayList of Tag
     */
    private ArrayList<Tag> parseTags(String[] tags){
        if(this.tags == null)
            return null;

        ArrayList<Tag> out = new ArrayList<>();

        int i=0;
        // Run through the tag names
        while(i<tags[i].length()){
            int k=0;
            // For each name, check if it exists in the Tag ArrayList of the class
            while(k<this.tags.size()){
                // If I found the tag (compare the names)
                if(
                        tags[i].equals(
                                this.tags.get(i).getProp(
                                        tagsTableColumns[0][0])
                        )
                )
                    // Add the tag to the output list
                    out.add(this.tags.get(i));

                k++;
            }
            i++;
        }

        return out;
    }

    /**
     * Add a new Document to the archive
     * @param tagStrings A String array containing the names of the tag that belong to the document
     * @param name Name of the document
     * @param path Path of the file
     */
    public void addDocument(String[] tagStrings, String name, Path path){

        if(documentExists(name, path.toString()))
            return;

        // Parse the name of the tags and get the actual objects
        ArrayList<Tag> tags = parseTags(tagStrings);

        // Create the document
        Document document = new Document(tags, name, path);

        // For each tag add to its own document list the document
        int i=0;
        if(tags != null)
            while (i < tags.size()) {
                tags.get(i).addDocument(document);
                i++;
            }

        // Create the data array to be given to the SQL query
        String[][] data = new String[mainTableColumns.length][2];

        i=0;
        while(i< mainTableColumns.length){
            data[i][0] = mainTableColumns[i][0];
            data[i][1] = document.getProp(mainTableColumns[i][0]);
            i++;
        }

        // Add the document to the db
        int id = db.addRow(mainTable, data);
        document.setID(id);

        // Add the document ID (reference to the main table) to each table of each tag
        if(tags!=null) {
            i=0;
            while ( i<tags.size ())
                db.addRow(tagStrings[i], new String[][]{{tagColumns[0][0], Integer.toString(document.getID())}});
        }
    }

    public void addDocument(String name, Path path){
        addDocument(null, name, path);
    }

    /**
     * Check if a tag already exists
     * @param name Name of the tag
     * @return True if it exists, otherwise false
     */
    boolean tagExists(String name){
        return tagTree.searchNode(name);
    }

    /**
     * Check if a document is already in the database
     * @param name Name of the document
     * @param path Path of the document
     * @return True of it's already there, otherwise false
     */
    boolean documentExists(String name, String path){
        if(documents==null)
            return false;
        int i=0;
        while(i < documents.size()){
            if(documents.get(i).getProp(mainTableColumns[0][0]).equals(name) &&
                    documents.get(i).getProp(mainTableColumns[1][0]).equals(path))
                return true;
            i++;
        }
        return false;
    }

    /**
     * Add a new tag to the archive
     * @param documents The list of the documents that belongs to the new tag
     * @param name Name of the tag
     */
    public void addTag(ArrayList<Document> documents, String name){

        // If the tag already exists
        if(tagExists(name))
            return;

        // Create the tag
        Tag tag = new Tag(documents, name);

        // Add each document to the tag's own document list
        int i=0;
        if(documents != null)
            while (i < documents.size()) {
                documents.get(i).addTag(tag);
                i++;
            }

        // Create the data array to be given to the SQL query
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

    /**
     * Gets the document list from the Derby database and updates the current ArrayList
     */
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

    /**
     * Gets all the tags from the tags Derby database and updates the tag list
     */
    void updateTagsFromDB(){

        // Get the table data
        ResultSet rs = db.getAllFromTable(tagsTable);

        try {

            // Create the list
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

    public void init(){
        updateTagsFromDB();
        updateDocumentsFromDB();
    }

    public ArrayList<Document> getDocuments(){
        return documents;
    }

}
