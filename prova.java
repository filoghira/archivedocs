import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSetMetaData;

public class documenti{
    //jdbc connection

    public static void main(String[] args){
        Connection conn = DriverManager.getConnection("jdbc:derby://localhost:1527/prova1");
        Statement stmt = conn.createStatement();
        st.executeUpdate();
        conn.close();

    }

}