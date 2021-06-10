package Main;

import java.util.ArrayList;

public class Tag {

    private ArrayList<Document> documents;
    private String name;

    public Tag(String name){
        this.name = name;
        documents = new ArrayList<>();
    }

    boolean addDocument(Document document){
        if(documents.contains(document))
            return false;
        documents.add(document);
        return true;
    }

}
