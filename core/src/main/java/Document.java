import java.nio.file.Path;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Document {

    private final List<Tag> tags;
    private final String hash;
    private String name;
    private Path path;
    private Timestamp lastEdit;
    private long size;
    private String description;
    private int ID;

    public Document(int ID, List<Tag> tags, String name, Path path, String hash, String description, long size, Timestamp lastEdit){
        this.ID = ID;
        this.tags = tags;
        this.name = name;
        this.path = path;
        this.hash = hash;
        this.description = description;
        this.size = size;
        this.lastEdit = lastEdit;
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

    public String getLastEdit() {
        return new SimpleDateFormat("MM-dd-yyyy").format(new Date(lastEdit.getTime()));
    }

    public String getSize() {
        return size/1024 + "KB";
    }

    public String getName() {
        return name;
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
