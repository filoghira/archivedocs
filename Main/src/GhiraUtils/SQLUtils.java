package GhiraUtils;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUtils {
    public static void printSQLException(SQLException e)
    {

        if(e.getSQLState().equals("X0Y32")){
            System.out.println("The main table already exists, not creating it.");
            return;
        }

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
