public class DocumentsTable {
    public static Column fileName = new Column("fileName", "VARCHAR(260)");
    public static Column filePath = new Column("filePath", "VARCHAR(32672)");
    public static Column fileHash = new Column("fileHash", "VARCHAR(128)");
}