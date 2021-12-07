import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Archive {

    private HashMap<Integer, Document> documents;
    private Node tagTree = new Node(null);
    private Database db;
    private static final String documentsStorage = "\\docs";

    public Archive(String username, String password){
        db = new Database(username, password);
        init();
    }

    /**
     * Add a new Document to the archive
     * @param tags A list of the tags that are added to the document
     * @param name Name of the document
     * @param path Path of the file
     */
    public void addDocument(List<Tag> tags, String name, Path path, String description) throws FileNotFoundException, FileAlreadyInArchiveException {

        // Check if the file exists in the filesystem
        File tempFile = new File(path.toString());
        if(!tempFile.exists())
            throw new FileNotFoundException("The file named as "+name+" and located in "+path+" does not exist");

        // Hash the file
        String hash = null;
        try {
            hash = General.checksum(path.toString(), Database.cryptAlgorithm);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Check if there's a file with the same hash in the archive
        if(documentExists(hash))
            throw new FileAlreadyInArchiveException("The file named as "+name+" and located in "+path+" does not exist");

        // Create the data array to be given to the SQL query
        String[][] data = DocumentsTable.getData(name, String.valueOf(path), hash, description, tempFile.length(), new Timestamp(tempFile.lastModified()));

        String ext = name.split("\\.(?=[^.]+$)")[1];
        name = name.split("\\.(?=[^.]+$)")[0];

        // Create the document
        Document document = new Document(
                -2,
                tags,
                name,
                path,
                hash,
                description,
                tempFile.length(),
                ext,
                new Timestamp(tempFile.lastModified()),
                this);

        // Add the document to the db
        int id = db.addRow(DocumentsTable.name, data);
        document.setID(id);

        // Add the document ID (reference to the main table) to each table of each tag
        if(tags!=null) {
            int i=0;
            while ( i<tags.size()) {
                if (!tags.get(i).contains(document)) {
                    int new_id = db.addRow(tags.get(i).getName(), new String[][]{{TagColumns.mainID.name(), Integer.toString(document.getID()), TagColumns.mainID.type()}});
                    tags.get(i).addDocument(new_id, document);
                }
                i++;
            }
        }

        try {
            // Check if the folder where documents are stored has already been created
            Path docsDir = Paths.get(General.homePath() + Database.defaultFolder + documentsStorage);
            if(!Files.isDirectory(docsDir))
                Files.createDirectories(docsDir);
            // Copy the file in the folder
            Files.copy(path, Path.of(docsDir + "\\" + name));
        } catch (IOException e) {
            e.printStackTrace();
        }

        documents.put(id, document);
    }

    /**
     * Remove a document from the archive
     * @param document The document to be removed
     */
    public void removeDocument(Document document) {
        // Check if the document exists
        if(!documents.containsValue(document))
            return;

        // Remove the document from the archive
        documents.remove(document.getID());

        // Remove the document from the database
        db.deleteRow(DocumentsTable.name, document.getID());

        // Remove the document from the file system
        File file = document.getPath().toFile();

        if(file.exists())
            if(!file.delete())
                System.out.println("The document "+document+" located in "+document.getPath()+" was NOT deleted");
    }

    /**
     * Gets the document list from the Derby database and updates the current list
     */
    void updateDocumentsFromDB(){

        // SELECT every document from the main table in the database
        ResultSet rs = db.getAllFromTable(DocumentsTable.name);

        try {

            HashMap<Integer, Document> documents = new HashMap<>();

            if(rs!=null)
                // Go through result set and create documents
                while (rs.next()) {
                    int id = rs.getInt(1);
                    String fileName = rs.getString(2);
                    String filePath = rs.getString(3);
                    String hash = rs.getString(4);
                    String description = rs.getString(5);
                    long size = rs.getLong(6);
                    String ext = rs.getString(7);
                    Timestamp date = rs.getTimestamp(8);

                    documents.put(id, new Document(id,
                            null,
                            fileName,
                            Paths.get(filePath),
                            hash,
                            description,
                            size,
                            ext,
                            date,
                            this
                    ));
                }

            // Update the document list
            this.documents = documents;

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Check if a document is already in the database
     * @param hash Hash of the document to be searched
     * @return True of it's already there, otherwise false
     */
    public boolean documentExists(String hash){
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
     * @param name Name of the tag
     */
    public void addTag(String name, String parentName, String description) {
        if(parentName == null)
            parentName = "root";

        // Replace spaces with underscores
        name = name.replace(" ", "_");

        // If the tag already exists or if the parent tag doesn't
        if(tagTree.nodeExists(name) || !tagTree.nodeExists(parentName))
            return;

        // Create the tag
        Tag tag = new Tag(-2, name, description);

        // Add each document to the tag's own document list
        if(documents != null) {
            int i = 0;
            while (i < documents.size()) {
                documents.get(i).addTag(tag);
                i++;
            }
        }

        // Add the document to the db
        tag.setID(db.addRow(TagsTable.name, TagsTable.getData(name, Integer.toString(getTagID(parentName)), description)));

        // Create the table of the tag
        db.addTable(name, new Column[] {TagColumns.mainID});

        // Update the tag tree
        updateTagsFromDB();

        // Set the node
        tag.setNode(tagTree.getNode(tag.getName()));
    }

    /**
     * Remove a tag from the archive
     * @param tag Tag to be removed
     */
    public void removeTag(Tag tag) {
        // Check if the tag exists
        if(!tagTree.nodeExists(tag.getName()))
            return;

        // Remove the tag from the archive and retrieve the documents
        Tag parent = tag.getNode().getParent().getData();
        List<Document> documents = tagTree.removeNode(tag.getName());

        // Add each document to the parent tag
        for (Document document : documents)
            parent.addDocument(document.getID(), document);

        // Remove the tag from the database
        db.deleteRow(TagsTable.name, tag.getID());

        // Delete the table of the tag
        db.deleteTable(tag.getName());

    }

    /**
     * Gets all the tags from the tags Derby database and updates the tag tree
     */
    void updateTagsFromDB(){

        // Empty the tag tree
        tagTree = new Node(null);

        // Get the table data
        ResultSet rs = db.getAllFromTable(TagsTable.name);

        try {

            // List that contains the data extracted from the DB
            List<String[]> pendingTags = new ArrayList<>();

            if(rs!=null)
                // Go through result set and get the data
                while (rs.next())
                    pendingTags.add(new String[]{
                            String.valueOf(rs.getInt("ID")),
                            rs.getString(TagsTable.tagName.name()),
                            String.valueOf(rs.getInt(TagsTable.tagParentID.name())),
                            rs.getString(TagsTable.tagDesc.name())
                    });

            // Convert raw data in a list of tags
            while(!pendingTags.isEmpty())
                for (Iterator<String[]> iter = pendingTags.iterator(); iter.hasNext(); ) {
                    String[] rawTag = iter.next();
                    int ID = Integer.parseInt(rawTag[0]);
                    int parentID = Integer.parseInt(rawTag[2]);

                    Tag tag = new Tag(ID, rawTag[1], rawTag[3]);

                    // If the tag has root as parent
                    if(parentID == 0) {
                        Node n = new Node(tag);
                        n.setParent(tagTree);
                        tag.setNode(n);
                        tagTree.addChild(n);
                        iter.remove();
                    }else if(tagTree.nodeExists(parentID)){    // If the tag parent has already been created
                        Node parent = tagTree.getNode(Integer.parseInt(rawTag[2]));
                        Node n = new Node(tag);
                        n.setParent(parent);
                        tag.setNode(n);
                        parent.addChild(new Node(tag));
                        iter.remove();
                    }

                    // For each tag add its documents
                    ResultSet documents = db.getAllFromTable(rawTag[1]);
                    if(documents != null){
                        while(documents.next()) {
                            int docID = documents.getInt("ID");
                            Document d = getDocument(
                                    documents.getInt(TagColumns.mainID.name())
                            );
                            tag.addDocument(docID, d);
                            d.addTag(tag);
                        }
                    }
                }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void init(){
        updateDocumentsFromDB();
        updateTagsFromDB();
    }

    public List<Document> getDocuments(){
        return new ArrayList<>(documents.values());
    }

    public Node getTagTree(){
        return tagTree;
    }

    /**
     * Given a tag name, returns the tag's ID
     * @param name Name of the tag
     * @return The tag's ID
     */
    int getTagID(String name){
        if(name.equals("root"))
            return 0;
        Node n = tagTree.getNode(name);
        return n.getData().getID();
    }

    /**
     * Retrieve a Document object from the archive using the ID in the database
     * @param id ID in the database
     * @return th document
     */
    public Document getDocument(int id){
        return documents.get(id);
    }

    public void logout(){
        db.closeConnection();
        db = null;
    }

    /**
     * Edit a document
     * @param d The document to edit
     * @param newName The new name of the document
     * @param newDesc The new description of the document
     * @param newTags The new tags of the document
     */
    void editDocument(Document d, String newName, String newDesc, List<String> newTags){
        // Update the name
        d.setName(newName);
        // Update the description
        d.setDescription(newDesc);
        // Update the tags in the document
        List<Tag> oldTags = d.setTags(newTags);

        // Remove the old tags from the database
        for (Tag tag : oldTags) {
            int id = tag.getDocumentID(d);
            db.deleteRow(tag.getName(), id);
            tag.removeDocument(id);
        }
    }

    void editTag(Tag tag, String newName, String newDesc, String newParent){

        newName = newName.replace(" ", "_");

        // Update the name
        if(!newName.equals(tag.getName()) && !tagTree.nodeExists(newName)) {
            db.updateRow(TagsTable.name, tag.getID(), TagsTable.tagName, newName);
            db.renameTable(tag.getName(), newName);
            tag.setName(newName);
        }

        // Update the description
        if(!newDesc.equals(tag.getDescription())) {
            tag.setDescription(newDesc);
            db.updateRow(TagsTable.name, tag.getID(), TagsTable.tagDesc, newDesc);
        }

        // Update the parent
        if(newParent == null) return;
        Tag newParentVal = tagTree.getNode(newParent).getData();
        if(newParentVal != tag.getParent()) {
            tag.setParent(newParentVal);
            db.updateRow(TagsTable.name, tag.getID(), TagsTable.tagParentID, String.valueOf(newParentVal.getID()));
        }

    }
}