package Database;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Startup {

    static String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    static String protocol = "jdbc:derby:";

    public static boolean init(String userName, String password) throws InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, SQLException {
        // Location of the database
        String path = Utilities.HomeDir.homePath() + "\\" + userName;

        // Initialize the Derby driver
        Class.forName(driver).getDeclaredConstructor().newInstance();

        // Try/catch with SQLException to understand if credentials are correct
        // https://stackoverflow.com/questions/40925635/drivermanager-getconnection-how-can-i-determine-whether-the-username-password
        Connection conn = DriverManager.getConnection(protocol + path + ";create=true", userName, password);

        return true;
    }
}
