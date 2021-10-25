package Database;

public class Column{
    private final String name;
    private final String type;

    public Column(String name, String type){
        this.name = name;
        this.type = type;
    }

    public String name(){
        return name;
    }

    public String type(){
        return type;
    }
}
