package comp.rest.config;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static String url      = "jdbc:postgresql://localhost:5432/restaurant_db";
    private static String user     = "postgres";
    private static String password = "1234";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Warning: PostgreSQL JDBC Driver not found in classpath.");
        }

        try (InputStream input = new FileInputStream("db.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            if (prop.getProperty("db.url") != null)      url      = prop.getProperty("db.url").trim();
            if (prop.getProperty("db.user") != null)     user     = prop.getProperty("db.user").trim();
            if (prop.getProperty("db.password") != null) password = prop.getProperty("db.password").trim();
        } catch (Exception ignored) {
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
