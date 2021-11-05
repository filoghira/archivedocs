public class DocumentsTable {
    public static Column fileName = new Column("fileName", "VARCHAR(260)");
    public static Column filePath = new Column("filePath", "VARCHAR(32672)");
    public static Column fileHash = new Column("fileHash", "VARCHAR(128)");
    public static Column fileDesc = new Column("fileDesc", "CLOB");

    public static String[][] getData(String name, String path, String hash, String description){
        return new String[][]{
                {fileName.name(), name, fileName.type()},
                {filePath.name(), path, filePath.type()},
                {fileHash.name(), "", fileHash.type()},
                {fileDesc.name(), description, fileDesc.type()}
        };
    }

    public static Column[] getColumns(){
        return new Column[]{
                fileName,
                filePath,
                fileHash,
                fileDesc
        };
    }
}