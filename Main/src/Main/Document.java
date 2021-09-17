package Main;

import java.nio.file.Path;
import java.util.List;

public class Document {

    private List<Tag> tags;
    private String name;
    private Path path;
    private int ID;

    public Document(List<Tag> tags, String name, Path path){
        this.tags = tags;
        this.name = name;
        this.path = path;
    }

    /**
     * Add a tag to the document
     * @param tag Tag to be added
     * @return False if the document has already the tag, otherwise returns true.
     */
    boolean addTag(Tag tag){
        if(tags.contains(tag))
            return false;
        tags.add(tag);
        return true;
    }

    public String getName(){
        return name;
    }

    public Path getPath(){
        return path;
    }

    public void setID(int ID){
        this.ID = ID;
    }

    public int getID(){
        return ID;
    }

}
