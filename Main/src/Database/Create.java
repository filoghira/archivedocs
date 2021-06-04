package Database;

import java.beans.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Create {
    private static Connection conn = null;
    public static void CreaTabella(String par[]) throws Exception {
        Statement state = (Statement) conn.createStatement();

        String query = "CREATE TABLE " + par[0] + "("
                + "Id INT NOT NULL GENERATED ALWAYS AS IDENTITY, ";

        int i=1, limit = par.length;
        while(i < limit){
            query = query + par[i] + " VARCHAR(255), ";
            i++;
        }

        query = query + "PRIMARY KEY (Id))";

        try {
            state.execute(query);
            System.out.println("table created");
        }catch(SQLException e){
            System.out.println("Error SQL:"+e.getErrorCode());
        }

    }
}
