package Database;

import java.sql.*;

public class Startup {

    static String protocol = "jdbc:derby:";

    public static Connection init(String userName, String password){

        Connection conn = null;

        try{

            String homePath = GeneralUtils.HomeDir.homePath();

            conn = DriverManager.getConnection(protocol + homePath + "\\" + userName
                                                            + ";create=true"
                                                            + ";user=" + userName
                                                            + ";password=" + password);

            return conn;
        } catch (SQLException sqle) {
            printSQLException(sqle);
        }

        return null;
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
