package infrastructure.jdbc;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class Db {
    private static final String url;
    private static final String user;
    private static final String pass;

    static {
        try (InputStream in = Db.class.getClassLoader().getResourceAsStream("db.properties")) {
            Properties p = new Properties();
            if (in == null) {
                throw new IllegalStateException(
                        "db.properties not found on classpath. " +
                                "Make sure it is inside src/main/resources (marked as Resources Root)."
                );
            }
            p.load(in);

            String tmpUrl = p.getProperty("db.url");
            String tmpUser = p.getProperty("db.user");
            String tmpPass = p.getProperty("db.password", "");

            if (isBlank(tmpUrl) || isBlank(tmpUser)) {
                throw new IllegalStateException("db.url and db.user must be set in db.properties");
            }

            url = tmpUrl.trim();
            user = tmpUser.trim();
            pass = tmpPass; // allow empty password

            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Failed to init DB: " + e.getMessage());
        }
    }

    private Db() {}

    public static Connection get() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
