public class TagsTable{
    public static String name = "tag";
    public static Column tagName = new Column("tagName", "VARCHAR(128)");
    public static Column tagParentID = new Column("tagParentID", "INT");
    public static Column tagDesc = new Column("tagDesc", "CLOB");

    public static String[][] getData(String name, String id, String description){
        return new String[][]{
                {tagName.name(), name, tagName.type()},
                {tagParentID.name(), id, tagParentID.type()},
                {tagDesc.name(), description, tagDesc.type()}
        };
    }

    public static Column[] getColumns(){
        return new Column[]{
                tagName,
                tagParentID,
                tagDesc
        };
    }
}