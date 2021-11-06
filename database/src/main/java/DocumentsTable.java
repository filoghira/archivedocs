import java.sql.Timestamp;

public class DocumentsTable {
    public static Column fileName = new Column("fileName", "VARCHAR(260)");
    public static Column filePath = new Column("filePath", "VARCHAR(32672)");
    public static Column fileHash = new Column("fileHash", "VARCHAR(128)");
    public static Column fileDesc = new Column("fileDesc", "CLOB");
    public static Column fileSize = new Column("fileSize", "BIGINT");
    public static Column lastEdit = new Column("lastEdit", "TIMESTAMP");
    // TODO: 06/11/2021 Implementare la gestione del tipo del file, rimuovendo quindi l'estensione quando il documento viene aggiunto

    public static String[][] getData(String name, String path, String hash, String description, long size, Timestamp date){
        return new String[][]{
                {fileName.name(), name, fileName.type()},
                {filePath.name(), path, filePath.type()},
                {fileHash.name(), hash, fileHash.type()},
                {fileDesc.name(), description, fileDesc.type()},
                {fileSize.name(), String.valueOf(size), fileSize.type()},
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
                lastEdit
        };
    }
}