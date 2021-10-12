package Main;

import Database.*;
import GhiraUtils.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
    private final Node tagTree = new Node(null);
    private final DatabaseUtilities db;
    private static final String documentsStorage = "\\docs";

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
    public void addDocument(List<Tag> tags, String name, Path path) throws FileNotFoundException, FileAlreadyInArchiveException {

        // Check if the file exists in the filesystem
        File tempFile = new File(path.toString());
        if(!tempFile.exists())
            throw new FileNotFoundException("The file named as "+name+" and located in "+path+" does not exist");

        // Hash the file
        String hash = null;
        try {
            hash = General.checksum(path.toString(), "SHA-512");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Check if there's a file with the same hash in the archive
        if(isInArchive(hash))
            throw new FileAlreadyInArchiveException("The file named as "+name+" and located in "+path+" does not exist");

        // Create the document
        Document document = new Document(-2, tags, name, path, hash);

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
                DocumentsTable.fileName.name(),
                name,
                DocumentsTable.fileName.type()
            },
            {
                DocumentsTable.filePath.name(),
                path.toString(),
                DocumentsTable.filePath.type()
            },
            {
                DocumentsTable.fileHash.name(),
                hash,
                DocumentsTable.fileHash.type()
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

        try {
            // Check if the folder where documents are stored has already been created
            Path docsDir = Paths.get(General.homePath() + defaultFolder + documentsStorage);
            if(!Files.isDirectory(docsDir))
                Files.createDirectories(docsDir);
            // Copy the file in the folder
            Files.copy(path, Path.of(docsDir + "\\" + name + "_" + id));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addDocument(String name, Path path) throws FileNotFoundException, FileAlreadyInArchiveException {
        addDocument(null, name, path);
    }

    public void removeDocument(Document document) {
        if(!documents.contains(document))
            return;

        documents.remove(document);

        db.deleteRow(mainTable, document.getID());
    }

    /**
     * Check if a document is already in the database
     * @param hash Hash of the document to be searched
     * @return True of it's already there, otherwise false
     */
    boolean isInArchive(String hash){
        if(documents==null)
            return false;
        int i=0;
        while(i < documents.size()){
            if(documents.get(i).compareHash(hash))
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
        if(tagTree.nodeExists(name) || !tagTree.nodeExists(parentName))
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
                int id = rs.getInt(1);
                String fileName = rs.getString(2);
                String filePath = rs.getString(3);
                String hash = rs.getString(4);

                documents.add(new Document(id, null, fileName, Paths.get(filePath), hash));
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

    /**
     * Retrieve a Document object from the archive using the ID in the database
     * @param id ID in the database
     * @return th document
     */
    public Document getDocument(int id){
        for (Document d:documents)
            if(d.getID()==id)
                return d;
        return null;
    }
}
