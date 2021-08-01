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
    private Connection connection = null;

    public static final String mainTable = "rootTable", tagsTable = "tagTable";
    public static final String[][] mainTableColumns = {{"fileName", "VARCHAR(260)"}, {"filePath", "VARCHAR(32672)"}}, tagsTableColumns = {{"tagName", "VARCHAR(100)"}}, tagColumns = {{"main_ID", "INT"}};
    private static final String defaultFolder = "\\archivedocs";

    public Database (String userName, String password)
    {

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
            addTable(tagsTable, tagsTableColumns);

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

        // Create the statement
        PreparedStatement pstmt;
        try {
            // Execute the query and close it
            pstmt = connection.prepareStatement(query.toString(), PreparedStatement.RETURN_GENERATED_KEYS );
            System.out.println("Executing query:\n" + query);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            SQLUtils.printSQLException(e);
        }
    }

    /**
     * Insert a row in a table
     * @param tableName Name of the table
     * @param values String matrix. Each row contains the name of the column and the data.
     * @return Returns false if something goes wrong, otherwise it returns true.
     */
    public int addRow(String tableName, String[][] values){

        // Prepare the query
        StringBuilder query = new StringBuilder("INSERT INTO " + tableName + " (");
        StringBuilder partialQuery = new StringBuilder(" (");

        // Run through the values
        int i=0;
        while(i < values.length){
            // Build two separated strings, one for the columns and the other for the values
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

        // Concatenate the strings
        query.append(partialQuery);
        // Prepare the statement
        PreparedStatement pstmt;

        try {
            // Execute the statement and get as result the ID of the item that has just been added
            pstmt = connection.prepareStatement(query.toString(), PreparedStatement.RETURN_GENERATED_KEYS );
            System.out.println("Executing query:\n" + query);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            rs.next();
            int out = rs.getInt(1);
            pstmt.close();
            return out;
        } catch (SQLException e) {
            SQLUtils.printSQLException(e);
        }
        return -1;
    }

    /**
     *
     * @param tableName Name of the table
     * @return A resultSet containing all of the data in the table
     */
    public ResultSet getAllFromTable(String tableName){
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

    /**
     * Close the connection of the database
     */
    public void closeConnection(){
        try {
            connection.close();
        } catch (SQLException e) {
            SQLUtils.printSQLException(e);
        }
    }

}
