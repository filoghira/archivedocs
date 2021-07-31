package Main;

import java.nio.file.Path;
import java.util.ArrayList;

import static Database.Database.mainTableColumns;

public class Document {

    private ArrayList<Tag> tags;
    private String name;
    private Path path;
    private int ID;

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
        if(prop.equals(mainTableColumns[0][0]))
            return name;
        else if(prop.equals(mainTableColumns[1][0]))
            return path.toString();
        else
            return null;
    }

    public void setID(int ID){
        this.ID = ID;
    }

    public int getID(){
        return ID;
    }

}
