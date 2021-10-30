package Main;

import java.util.ArrayList;
import java.util.List;

public class Tag {

    private List<Document> documents = new ArrayList<>();
    private String name;
    private int ID;

    public Tag(int ID, List<Document> documents, String name){
        this.ID = ID;
        this.name = name;
        this.documents = documents;
    }

    public Tag(int ID, String name){
        this.ID = ID;
        this.name = name;
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
        if(documents.contains(document))
            return false;
        documents.add(document);
        return true;
    }

    public String getName(){
        return name;
    }

    public boolean contains(Document document){
        return documents.contains(document);
    }

    public void addDocumentToParent(Document document) {
        //if(parent != null)
    }
}
