package Main;

import Database.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static Database.DatabaseUtilities.*;

public class Archive {

    private List<Document> documents;
    private Node tagTree = new Node(null);
    private DatabaseUtilities db;

    public Archive(String username, String password){
        db = new DatabaseUtilities(username, password);
        init();
    }

    public Archive(String username){
        db = new DatabaseUtilities(username, "");
        init();
    }

    /**
     * Add a new Document to the archive
     * @param tags A list of the tags that are added to the document
     * @param name Name of the document
     * @param path Path of the file
     */
    public void addDocument(List<Tag> tags, String name, Path path){

        if(documentExists(name, path.toString()))
            return;

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
        String[][] data = new String[][] {
            {
                MainTable.fileName.name(),
                name
            },
            {
                MainTable.filePath.name(),
                path.toString()
            }
        };

        // Add the document to the db
        int id = db.addRow(mainTable, data);
        document.setID(id);

        // Add the document ID (reference to the main table) to each table of each tag
        if(tags!=null) {
            i=0;
            while ( i<tags.size ())
                db.addRow(tags.get(i).getName(), new String[][]{{TagColumns.mainID.name(), Integer.toString(document.getID())}});
        }
    }

    public void addDocument(String name, Path path){
        addDocument(null, name, path);
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
            if(documents.get(i).getName().equals(name) &&
                    documents.get(i).getPath().toString().equals(path))
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
    public void addTag(List<Document> documents, String name, String parentName){

        // If the tag already exists
        if(tagTree.nodeExists(name))
            return;

        // Create the tag
        Tag tag = new Tag(-2, documents, name);

        // Add each document to the tag's own document list
        if(documents != null) {
            int i = 0;
            while (i < documents.size()) {
                documents.get(i).addTag(tag);
                i++;
            }
        }

        // Create the data array to be given to the SQL query
        String[][] data = {
                {TagsTable.tagName.name(), name, TagsTable.tagName.type()},
                {TagsTable.tagParentID.name(), Integer.toString(getTagID(parentName)), TagsTable.tagParentID.type()}
            };

        // Add the document to the db
        tag.setID(db.addRow(tagsTable, data));

        // Create the table of the tag
        db.addTable(name, new Column[] {TagColumns.mainID});

        updateTagsFromDB();
    }

    public void addTag(String name){
        addTag(name, "root");
    }

    public void addTag(String name, String parentName){
        addTag(null, name, parentName);
    }

    /**
     * Gets the document list from the Derby database and updates the current list
     */
    void updateDocumentsFromDB(){

        // SELECT every document from the main table in the database
        ResultSet rs = db.getAllFromTable(mainTable);

        try {

            List<Document> documents = new ArrayList<>();

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

    int getTagID(String name){
        if(name.equals("root"))
            return 0;
        Node n = tagTree.getNode(name);
        return n.getData().getID();
    }

    /**
     * Gets all the tags from the tags Derby database and updates the tag tree
     */
    void updateTagsFromDB(){

        // Get the table data
        ResultSet rs = db.getAllFromTable(tagsTable);

        try {

            // List that contains the data extracted from the DB
            List<String[]> pendingTags = new ArrayList<>();

            // Go through result set and get the data
            while (rs.next())
                pendingTags.add(new String[]{
                        String.valueOf(rs.getInt("ID")),
                        rs.getString(TagsTable.tagName.name()),
                        String.valueOf(rs.getInt(TagsTable.tagParentID.name()))
                });

            // Convert raw data in a list of tags
            while(!pendingTags.isEmpty())
                for (Iterator<String[]> iter = pendingTags.iterator(); iter.hasNext(); ) {
                    String[] rawTag = iter.next();
                    int ID = Integer.parseInt(rawTag[0]);
                    int parentID = Integer.parseInt(rawTag[2]);

                    // If the tag has root as parent
                    if(parentID == 0) {
                        tagTree.addChild(new Node(new Tag(ID, rawTag[1])));
                        iter.remove();
                    }else if(tagTree.nodeExists(Integer.parseInt(rawTag[2]))){    // If the tag parent has already been created
                        tagTree.getNode(Integer.parseInt(rawTag[2])).addChild(new Node(new Tag(ID, rawTag[1])));
                        iter.remove();
                    }
                }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void init(){
        updateTagsFromDB();
        updateDocumentsFromDB();
    }

    public List<Document> getDocuments(){
        return documents;
    }

    public void printTagTree(){
        tagTree.print(0);
    }

}
