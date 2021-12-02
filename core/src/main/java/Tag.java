import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Tag {

    private HashMap<Integer, Document> documents = new HashMap<>();
    private String name;
    private String description;
    private int ID;
    private Node node;

    public Tag(int ID, String name, String description){
        this.ID = ID;
        this.name = name;
        this.description = description;
    }

    public void setID(int ID){
        this.ID = ID;
    }

    public int getID(){
        return ID;
    }

    /**
     * Add a document to the tag
     * @param document The document to be added
     */
    void addDocument(int id, Document document){
        if(document==null || documents.containsValue(document) || documents.containsKey(id))
            return;
        documents.put(id, document);
    }

    public String getName(){
        return name;
    }

    public boolean contains(Document document){
        if (documents == null)
            return false;
        return documents.containsValue(document);
    }

    public void setDocuments(HashMap<Integer, Document> documents) {
        this.documents = documents;
    }

    /**
     * Get the documents in the tag
     * @return List of documents
     */
    public List<Document> getDocuments(){
        List<Document> documents = new ArrayList<>(this.documents.values());
        for (Node n: node.getChildren()) {
            Tag t = n.getData();
            documents.addAll(t.getDocuments());
        }
        return documents;
    }

    /**
     * Get the id in the tag table (NOT the reference to the main table)
     * @param document The document
     * @return The id in the tag table
     */
    int getDocumentID(Document document) {
        for (int id: documents.keySet())
            if (documents.get(id).equals(document))
                return id;
        return -1;
    }

    /**
     * Set the tag's node
     * @param node The node
     */
    public void setNode(Node node) {
        this.node = node;
    }

    public Node getNode(){
        return node;
    }

    public static List<Tag> getTags(List<String> tags){
        List<Tag> result = new ArrayList<>();
        for(String tag: tags){
            result.add(new Tag(0, tag, ""));
        }
        return result;
    }

    void removeDocument(int id) {
        documents.remove(id);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public Tag getParent(){
        return node.getParent().getData();
    }

    public void setParent(Tag newParent) {
        node.setParent(newParent.getNode());
    }
}
