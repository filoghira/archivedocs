import java.awt.*;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Document {

    private List<Tag> tags;

    private final String hash;

    private int ID;
    private String name;
    private final Path path;
    private final Timestamp lastEdit;
    private final long size;
    private String description;
    private final String ext;

    private final Archive archive;

    public Document(int ID, List<Tag> tags, String name, Path path, String hash, String description, long size, String ext, Timestamp lastEdit, Archive archive) {
        this.ID = ID;

        this.tags = tags;
        if (tags == null)
            this.tags = new ArrayList<>();

        this.name = name;
        this.path = path;
        this.hash = hash;
        this.description = description;
        this.size = size;
        this.ext = ext;
        this.lastEdit = lastEdit;

        this.archive = archive;
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

    public boolean contains(Tag tag){
        return tags.contains(tag);
    }

    public void setName(String text) {
        name = text;
    }

    public void setDescription(String text) {
        description = text;
    }

    /**
     * Set the tags of the document
     * @param tags Tags to be set
     * @return Old tags
     */
    public List<Tag> setTags(List<String> tags) {
        List<Tag> oldTags = new ArrayList<>(this.tags);

        // Remove all tags
        this.tags.clear();

        // Add all new tags
        if (tags != null)
            for (String tag : tags)
                this.tags.add(archive.getTagTree().getNode(tag).getData());

        return oldTags;
    }

    public String getDescription() {
        return description;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public String getExt() {
        return ext;
    }

}
