package Database;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
// import java.util.Properties;

import static GeneralUtils.SQLUtils.printSQLException;

public class Database {

    static String protocol = "jdbc:derby:";

    private String dbName;
    // private Properties dbProperties;
    private Connection connection = null;

    public Database (String userName, String password)
    {

        // dbProperties = loadDBProperties();

        dbName = userName;
        loadDatabaseDriver("org.apache.derby.jdbc.EmbeddedDriver");

        try{

            String homePath = GeneralUtils.HomeDir.homePath();

            connection = DriverManager.getConnection(protocol + homePath + "/" + userName
                    + ";create=true"
                    + ";user=" + userName
                    + ";password=" + password);

        } catch (SQLException sqle) {
            printSQLException(sqle);
        }

    }

    /*
    private Properties loadDBProperties()
    {
        InputStream dbPropInputStream = null;
        dbPropInputStream = Database.class.getResourceAsStream("Configuration.properties");
        dbProperties = new Properties();

        try {
            dbProperties.load(dbPropInputStream);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return dbProperties;
    }
     */

    private void loadDatabaseDriver(String driverName)
    {
        // Load the Java DB driver
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public void createTable(String name, ArrayList<String[]> col) throws Exception {

        // Create the statement
        Statement state = connection.createStatement();

        // Prepare the table for the creation of a table with ID as primary key
        String query = "CREATE TABLE " + name + "("
                + "Id INT NOT NULL GENERATED ALWAYS AS IDENTITY, ";

        // Run through the list of columns and update the query
        int i=0;
        while(i < col.size()){
            String[] element = col.get(i);
            query = query + element[0] + " " + element[1] + ", ";
            i++;
        }

        query += "PRIMARY KEY (Id))";

        // Execute the query
        try {
            state.execute(query);
        }catch(SQLException e){
            System.out.println("Error SQL:"+e.getErrorCode());
        }

    }

}
