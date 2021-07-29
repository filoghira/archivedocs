package Main;

import Database.Database;

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
