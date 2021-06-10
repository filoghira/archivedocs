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

}