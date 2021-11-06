import java.util.ArrayList;
import java.util.List;

public class Tag {

    private List<Document> documents;
    private String name;
    private String description;
    private int ID;
    private Node node;

    public Tag(int ID, List<Document> documents, String name, String description){
        this.ID = ID;
        this.name = name;
        this.documents = documents;
        this.description = description;
        if(documents==null)
            this.documents = new ArrayList<>();
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
     * @return False if the document has already the tag, otherwise returns true.
     */
    boolean addDocument(Document document){
        if(document==null || documents.contains(document))
            return false;
        documents.add(document);
        return true;
    }

    public String getName(){
        return name;
    }

    public boolean contains(Document document){
        if (documents == null)
            return false;
        return documents.contains(document);
    }

    public List<Document> getDocuments(){
        List<Document> documents = this.documents;
        for (Node n: node.getChildren()) {
            Tag t = n.getData();
            documents.addAll(t.getDocuments());
        }
        return documents;
    }

    public void setNode(Node node) {
        this.node = node;
    }
}
