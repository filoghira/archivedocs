import java.sql.Timestamp;

public class DocumentsTable {
    public static String name = "documents_table";

    public static Column fileName = new Column("fileName", "VARCHAR(260)"); // 2
    public static Column filePath = new Column("filePath", "VARCHAR(32672)");   // 3
    public static Column fileHash = new Column("fileHash", "VARCHAR(128)"); // 4
    public static Column fileDesc = new Column("fileDesc", "CLOB"); // 5
    public static Column fileSize = new Column("fileSize", "BIGINT");   // 6
    public static Column ext = new Column("ext", "VARCHAR(260)"); // 7
    public static Column lastEdit = new Column("lastEdit", "TIMESTAMP"); // 8

    public static String[][] getData(String name, String path, String hash, String description, long size, Timestamp date){
        return new String[][]{
                {fileName.name(), name.split("\\.(?=[^.]+$)")[0], fileName.type()},
                {filePath.name(), path, filePath.type()},
                {fileHash.name(), hash, fileHash.type()},
                {fileDesc.name(), description, fileDesc.type()},
                {fileSize.name(), String.valueOf(size), fileSize.type()},
                {ext.name(), name.split("\\.(?=[^.]+$)")[1], ext.type()},
                {lastEdit.name(), String.valueOf(date), lastEdit.type()}
        };
    }

    public static Column[] getColumns(){
        return new Column[]{
                fileName,
                filePath,
                fileHash,
                fileDesc,
                fileSize,
                ext,
                lastEdit
        };
    }
}