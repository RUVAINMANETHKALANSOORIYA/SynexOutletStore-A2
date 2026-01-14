package infrastructure.jdbc;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class TestDbConnection {
    public static void main(String[] args) {
        try {
            // Load properties
            InputStream in = TestDbConnection.class.getClassLoader().getResourceAsStream("db.properties");
            if (in == null) {
                System.out.println("❌ db.properties not found");
                System.exit(1);
            }

            Properties p = new Properties();
            p.load(in);

            String url = p.getProperty("db.url");
            String user = p.getProperty("db.user");
            String password = p.getProperty("db.password", "");

            System.out.println("🔧 Testing MySQL JDBC Driver...");

            // Test driver loading
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ MySQL JDBC Driver loaded successfully!");

            // Test connection
            System.out.println("🔌 Testing database connection...");
            System.out.println("   URL: " + url);
            System.out.println("   User: " + user);

            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Database connection successful!");

            // Test simple query
            var stmt = conn.createStatement();
            var rs = stmt.executeQuery("SELECT 1 as test");
            if (rs.next()) {
                System.out.println("✅ Database query test successful!");
            }

            rs.close();
            stmt.close();
            conn.close();

            System.out.println("\n🎉 All database tests passed!");

        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found: " + e.getMessage());
            System.err.println("   Make sure mysql-connector-j-8.2.0.jar is in WEB-INF/lib/");
            System.exit(1);
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            System.err.println("   Make sure MySQL server is running and database exists");
            System.exit(1);
        }
    }
}
