package GhiraUtils;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUtils {
    public static void printSQLException(SQLException e)
    {

        // Unwraps the entire exception chain to unveil the real cause of the exeption
        while (e != null)
        {

            if(e.getSQLState().equals("X0Y32")){
                System.out.println("The table already exists, not creating it.");
                return;
            }else if (e.getSQLState().equals("XSDB6")){
                System.out.println("Another connection to the database is opened. Closing the application...");
                System.exit(69);
            }

            System.err.println("\n----- SQLException -----");
            System.err.println("  SQL State:  " + e.getSQLState());
            System.err.println("  Error Code: " + e.getErrorCode());
            System.err.println("  Message:    " + e.getMessage());
            e = e.getNextException();
        }
    }

    public static String quote(String input){
        return "'" + input + "'";
    }

    public static void printResultSet(ResultSet rs, String[] columns){
        try {
            while (rs.next()) {
                for (String column : columns) System.out.print(column + " " + rs.getInt(column) + " ");
                System.out.println();
            }
            rs.close();
        }catch (SQLException e){
            printSQLException(e);
        }
    }

}
