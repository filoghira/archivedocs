import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

public class Database {

    static final String protocol = "jdbc:derby:";
    static final String driverName = "org.apache.derby.jdbc.EmbeddedDriver";
    public static final String cryptAlgorithm = "SHA-512";

    private final String dbName;
    private Connection connection = null;

    public static final String mainTable = "document";
    public static final String defaultFolder = "\\archivedocs";

    public Database(String userName, String password)
    {

        // Set derby preallocator to 1
        System.setProperty("derby.language.sequence.preallocator", String.valueOf(1));
        dbName = userName;
        loadDatabaseDriver(driverName);

        try{

            String homePath = General.homePath() + defaultFolder;

            // If the main directory where the databases are stored does not exist, create it
            Path archivesDir = Paths.get(homePath);
            if(!Files.isDirectory(archivesDir))
                Files.createDirectories(archivesDir);

            // Try to connect to the database. If the connection fails it creates a new database
            connection = DriverManager.getConnection(protocol + homePath + "\\" + userName
                    + ";create=true"
                    + ";user=" + userName
                    + ";password=" + password);

            // Checks if main table exists. If it doesn't it creates it
            addTable(mainTable, DocumentsTable.getColumns());
            addTable(TagsTable.name, TagsTable.getColumns());

        } catch (SQLException e) {
            SQLUtils.printSQLException(e);
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
     * Create an SQL table. By default, there's an Identity Column
     * @param name Name of the table
     * @param col String matrix. Each row contains the name of the column and the data type
     */
    public void addTable(String name, Column[] col){

        // Prepare the query
        StringBuilder query = new StringBuilder("CREATE TABLE " + name + " ("
                + "Id INT NOT NULL GENERATED ALWAYS AS IDENTITY(Start with 1, Increment by 1), ");

        // Run through the list of columns and update the query
        int i=0;
        while(i < col.length){
            query.append(col[i].name()).append(" ").append(col[i].type()).append(", ");
            i++;
        }

        query.append("PRIMARY KEY(Id))");

        // Create the statement
        PreparedStatement statement;
        try {
            // Execute the query and close it
            statement = connection.prepareStatement(query.toString(), PreparedStatement.RETURN_GENERATED_KEYS );
            System.out.println("Executing query:\n" + query);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            SQLUtils.printSQLException(e);
        }
    }

    /**
     * Insert a row in a table
     * @param tableName Name of the table
     * @param values String matrix. Each row contains the name of the column and the data.
     * @return Returns the ID of the row
     */
    public int addRow(String tableName, String[][] values){
        // Prepare the query
        StringBuilder query = new StringBuilder("INSERT INTO " + tableName + " (");
        StringBuilder partialQuery = new StringBuilder(" (");

        // Run through the values
        int i=0;
        while(i < values.length){

            String val;

            if(values[i][2].contains("INT"))
                val = values[i][1];
            else
                val = General.quote(values[i][1]);

            // Build two different strings, one for the columns and the other for the values
            query.append(values[i][0]);
            partialQuery.append(val);

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
        PreparedStatement statement;

        try {
            // Execute the statement and get as result the ID of the item that has just been added
            System.out.println("Executing query:\n" + query);
            statement = connection.prepareStatement(query.toString(), PreparedStatement.RETURN_GENERATED_KEYS );
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            rs.next();
            int out = rs.getInt(1);
            statement.close();
            return out;
        } catch (SQLException e) {
            SQLUtils.printSQLException(e);
        }
        return -1;
    }

    /**
     * Delete a row in a table by a row's id
     * @param tableName Name of the table
     * @param id ID of the row
     */
    public void deleteRow(String tableName, int id){
        // Prepare the query
        StringBuilder query = new StringBuilder("DELETE FROM " + tableName + " WHERE ID="+id);

        // Prepare the statement
        PreparedStatement statement;

        try {
            // Execute the statement and get as result the ID of the item that has just been added
            System.out.println("Executing query:\n" + query);
            statement = connection.prepareStatement(query.toString(), PreparedStatement.RETURN_GENERATED_KEYS );
            statement.executeUpdate();
        } catch (SQLException e) {
            SQLUtils.printSQLException(e);
        }
    }

    /**
     *
     * @param tableName Name of the table
     * @return A resultSet containing all the data in the table
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