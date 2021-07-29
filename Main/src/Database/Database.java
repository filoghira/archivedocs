package Database;

import GhiraUtils.General;
import GhiraUtils.SQLUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

import static GhiraUtils.SQLUtils.printSQLException;

public class Database {

    static String protocol = "jdbc:derby:";

    private final String dbName;
    // private Properties dbProperties;
    private Connection connection = null;

    public static final String mainTable = "rootTable";
    public static final String[][] mainTableColumns = {{"fileName", "VARCHAR(260)"}, {"filePath", "VARCHAR(32672)"}};
    private static final String defaultFolder = "\\archivedocs";

    public Database (String userName, String password)
    {

        // dbProperties = loadDBProperties();

        dbName = userName;
        loadDatabaseDriver("org.apache.derby.jdbc.EmbeddedDriver");

        try{

            String homePath = General.homePath() + defaultFolder;

            // If the main directory where the databases are stored does not exists, create it
            Path archivesDir = Paths.get(homePath);
            if(!Files.isDirectory(archivesDir))
                Files.createDirectories(archivesDir);

            // try to connect to the database. If the connection fails it creates a new database
            connection = DriverManager.getConnection(protocol + homePath + "\\" + userName
                    + ";create=true"
                    + ";user=" + userName
                    + ";password=" + password);

            // Checks if main table exists. If it doesn't it creates it
            addTable(mainTable, mainTableColumns);

        } catch (SQLException e) {
            printSQLException(e);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void loadDatabaseDriver(String driverName)
    {
        // Load the Java DB driver
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public boolean executeStatement(String query){
        // Create the statement
        Statement state;

        // Execute the query
        try {
            state = connection.createStatement();
            System.out.println("Executing query:\n" + query);
            boolean res = state.execute(query);

            state.close();

            return res;
        }catch(SQLException e){
            SQLUtils.printSQLException(e);
        }

        return false;
    }

    /**
     * Create a SQL table. By default there's an Identity Column
     * @param name Name of the table
     * @param col String matrix. Each row contains the name of the column and the data type
     */
    public void addTable(String name, String[][] col){

        // Prepare the query
        StringBuilder query = new StringBuilder("CREATE TABLE " + name + " ("
                + "Id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), ");

        // Run through the list of columns and update the query
        int i=0;
        while(i < col.length){
            String[] element = col[i];
            query.append(element[0]).append(" ").append(element[1]).append(", ");
            i++;
        }

        query.append("PRIMARY KEY(Id))");

        executeStatement(query.toString());
    }

    /**
     * Insert a row in a table
     * @param tableName Name of the table
     * @param values String matrix. Each row contains the name of the column and the data.
     * @return Returns false if something goes wrong, otherwise it returns true.
     */
    public boolean addRow(String tableName, String[][] values){

        // Prepare the query
        StringBuilder query = new StringBuilder("INSERT INTO " + tableName + " (");
        StringBuilder partialQuery = new StringBuilder(" (");

        int i=0;
        while(i < values.length){
            query.append(values[i][0]);
            partialQuery.append(General.quote(values[i][1]));

            if(i < values.length - 1) {
                query.append(", ");
                partialQuery.append(", ");
            }else{
                query.append(") VALUES");
                partialQuery.append(")");
                break;
            }

            i++;
        }

        return executeStatement(query + partialQuery.toString());
    }

    public ResultSet selectAll(String tableName){
        String query = "SELECT * FROM " + tableName;

        // Create the statement
        Statement state;

        // Execute the query
        try {
            state = connection.createStatement();

            return state.executeQuery(query);
        }catch(SQLException e){
            SQLUtils.printSQLException(e);
        }

        return null;
    }

    public void closeConnection(){
        try {
            connection.close();
        } catch (SQLException e) {
            SQLUtils.printSQLException(e);
        }
    }

}
