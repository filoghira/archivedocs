public class TagsTable{
    public static Column tagName = new Column("tagName", "VARCHAR(100)");
    public static Column tagParentID = new Column("tagParentID", Database.INT);
    public static Column tagDesc = new Column("tagDesc", "CLOB");
}