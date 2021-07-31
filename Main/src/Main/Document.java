package Main;

import java.nio.file.Path;
import java.util.ArrayList;

public class Document {

    private ArrayList<Tag> tags;
    private String name;
    private Path path;

    public Document(ArrayList<Tag> tags, String name, Path path){
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

    /**
     * Get a property from the document, such as the name or its path.
     * @param prop The property
     * @return the property.
     */
    public String getProp(String prop){
        switch (prop){
            case "fileName":
                return name;
            case "filePath":
                return path.toString();
            default:
                return null;
        }
    }

}
