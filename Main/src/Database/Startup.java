package Database;

import java.sql.*;

public class Startup {

    static String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    static String protocol = "jdbc:derby:";

    public static boolean init(String userName, String password){

        Connection conn = null;

        try{

            String dbName = userName;
            String homePath = GeneralUtils.HomeDir.homePath();

            conn = DriverManager.getConnection(protocol + homePath + "\\" + dbName
                                                            + ";create=true"
                                                            + ";user=" + userName
                                                            + ";password=" + password);

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
