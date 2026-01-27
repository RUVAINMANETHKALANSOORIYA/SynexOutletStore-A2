import infrastructure.jdbc.Db;
import java.sql.Connection;

public class QuickDbTest {
    public static void main(String[] args) {
        try {
            System.out.println("🔧 Testing Db.get() method...");
            Connection conn = Db.get();
            System.out.println("✅ Database connection via Db.get() successful!");

            // Test a simple query
            var stmt = conn.createStatement();
            var rs = stmt.executeQuery("SELECT 1 as test");
            if (rs.next()) {
                System.out.println("✅ Query execution successful!");
            }
            rs.close();
            stmt.close();
            conn.close();

            System.out.println("\n🎉 MySQL JDBC Driver is working perfectly!");
            System.out.println("✅ The 'Driver class not found' error is RESOLVED!");

        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
