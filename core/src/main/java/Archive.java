import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Archive {

    private List<Document> documents;
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

        // Create the document
        Document document = new Document(-2, tags, name, path, hash, description, tempFile.length(), new Timestamp(tempFile.lastModified()));

        // Create the data array to be given to the SQL query
        String[][] data = DocumentsTable.getData(name, String.valueOf(path), hash, description, tempFile.length(), new Timestamp(tempFile.lastModified()));

        // Add the document to the db
        int id = db.addRow(DocumentsTable.name, data);
        document.setID(id);

        // Add the document ID (reference to the main table) to each table of each tag
        if(tags!=null) {
            int i=0;
            while ( i<tags.size()) {
                if (!tags.get(i).contains(document)) {
                    tags.get(i).addDocument(document);
                    db.addRow(tags.get(i).getName(), new String[][]{{TagColumns.mainID.name(), Integer.toString(document.getID()), TagColumns.mainID.type()}});
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
            if(name.contains(".pdf"))
                name = name.replace(".pdf", "_"+id+".pdf");
            Files.copy(path, Path.of(docsDir + "\\" + name));
        } catch (IOException e) {
            e.printStackTrace();
        }

        documents.add(document);
    }

    /**
     * Remove a document from the archive
     * @param document The document to be removed
     */
    public void removeDocument(Document document) {
        // Check if the document exists
        if(!documents.contains(document))
            return;

        // Remove the document from the archive
        documents.remove(document);

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

            List<Document> documents = new ArrayList<>();

            if(rs!=null)
                // Go through result set and create documents
                while (rs.next()) {
                    int id = rs.getInt(1);
                    String fileName = rs.getString(2);
                    String filePath = rs.getString(3);
                    String hash = rs.getString(4);
                    String description = rs.getString(5);
                    long size = rs.getLong(6);
                    Timestamp date = rs.getTimestamp(7);

                    documents.add(new Document(id, null, fileName, Paths.get(filePath), hash, description, size , date));
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
     * @param documents The list of the documents that belongs to the new tag
     * @param name Name of the tag
     */
    public void addTag(List<Document> documents, String name, String parentName, String description) {
        if(parentName == null)
            parentName = "root";

        // Replace spaces with underscores
        name = name.replace(" ", "_");

        // If the tag already exists or if the parent tag doesn't
        if(tagTree.nodeExists(name) || !tagTree.nodeExists(parentName))
            return;

        // Create the tag
        Tag tag = new Tag(-2, documents, name, description);

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

    public void removeTag(Tag tag) {
        // Check if the tag exists
        if(!tagTree.nodeExists(tag.getName()))
            return;

        // Remove the tag from the archive and retrieve the documents
        Tag parent = tag.getNode().getParent().getData();
        List<Document> documents = tagTree.removeNode(tag.getName());

        // Add each document to the parent tag
        for (Document document : documents) {
            db.addRow(parent.getName(), TagColumns.getData(document.getID()));
            parent.addDocument(document);
        }

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

                    Tag tag = new Tag(ID, null, rawTag[1], rawTag[3]);

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
                        while(documents.next())
                            tag.addDocument(getDocument(documents.getInt(2)));
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
        return documents;
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
        for (Document d:documents)
            if(d.getID()==id)
                return d;
        return null;
    }

    public void logout(){
        db.closeConnection();
        db = null;
    }
}