package GeneralUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUtils {
    public static void printSQLException(SQLException e)
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

    public static boolean tableExists(Connection connection, String tableName) throws SQLException
    {
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet resultSet = meta.getTables(null, null, tableName, new String[] {"TABLE"});

        return resultSet.next();
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
