package Core;

import java.nio.file.Path;
import java.util.List;

public class Document {

    private final List<Tag> tags;
    private final String hash;
    private final String name;
    private final Path path;
    private int ID;

    public Document(int ID, List<Tag> tags, String name, Path path, String hash){
        this.ID = ID;
        this.tags = tags;
        this.name = name;
        this.path = path;
        this.hash = hash;
    }

    /**
     * Add a tag to the document
     * @param tag Tag to be added
     */
    void addTag(Tag tag){
        if(!tags.contains(tag))
            tags.add(tag);
    }

    public String toString(){
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

    public boolean compareHash(String hash){
        return hash.equals(this.hash);
    }

}
