package Main;

import java.util.ArrayList;

import static Database.Database.tagsTableColumns;

public class Tag {

    private ArrayList<Document> documents;
    private String name;
    private Tag parent;

    public Tag(ArrayList<Document> documents, String name, Tag parent){
        this.name = name;
        this.documents = documents;
        this.parent = parent;
    }

    public Tag(String name, Tag parent){
        this.name = name;
        this.parent = parent;
    }

    public Tag(ArrayList<Document> documents, String name){
        this.documents = documents;
        this.name = name;
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

    /**
     * Get a property from the tag, such as its name.
     * @param prop The property
     * @return the property.
     */
    public String getProp(String prop){
        if(prop.equals(tagsTableColumns[0][0]))
            return name;
        else
            return null;
    }


}
