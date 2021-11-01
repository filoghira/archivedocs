public class TagsTable{
    public static Column tagName = new Column("tagName", "VARCHAR(100)");
    public static Column tagParentID = new Column("tagParentID", DatabaseUtilities.INT);
    public static final int size = 2;
}