package Database;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class Startup {

    static String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    static String protocol = "jdbc:derby:";

    public static boolean init(String userName, String password) throws InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException{

        Connection conn = null;

        try{
            // Connection properties
            Properties props = new Properties();

            props.put("user", userName);
            props.put("password", password);

            String dbName = userName;

            conn = DriverManager.getConnection(protocol + dbName + ";create=true", props);

        } catch (SQLException sqle) {
            printSQLException(sqle);
        } finally {

            //Release connection
            try {
                if (conn != null) {
                    //aggiunta istruzione SQL
                    conn.close();
                    conn = null;
                }
            } catch (SQLException sqle) {
                printSQLException(sqle);
            }
        }

        return true;
    }

    static void printSQLException(SQLException e)
    {
        // Unwraps the entire exception chain to unveil the real cause of the exeption
        while (e != null)
        {
            System.err.println("\n----- SQLException -----");
            System.err.println("  SQL State:  " + e.getSQLState());
            System.err.println("  Error Code: " + e.getErrorCode());
            System.err.println("  Message:    " + e.getMessage());
            e = e.getNextException();
        }
    }
}
