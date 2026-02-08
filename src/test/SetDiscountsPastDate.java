package test;

import infrastructure.jdbc.Db;
import java.sql.*;

/**
 * Set discount start dates to definitely be in the past
 */
public class SetDiscountsPastDate {
    
    public static void main(String[] args) {
        System.out.println("Setting discount start dates to past...");
        
        try (Connection conn = Db.get()) {
            
            // Update all discounts to start 1 hour ago
            String sql = "UPDATE discounts SET start_date = DATE_SUB(NOW(), INTERVAL 1 HOUR) WHERE discount_code IN ('LAPTOP20', 'CLOTHES15', 'TECH500')";
            
            try (Statement stmt = conn.createStatement()) {
                int updated = stmt.executeUpdate(sql);
                System.out.println("✓ Updated " + updated + " discount start dates to 1 hour ago");
            }
            
            // Verify the fix
            String verifySql = "SELECT discount_code, start_date, NOW() as current_time, (NOW() > start_date) as is_past FROM discounts WHERE discount_code IN ('LAPTOP20', 'CLOTHES15', 'TECH500')";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(verifySql)) {
                
                System.out.println("\nVerification:");
                while (rs.next()) {
                    System.out.printf("- %s: start=%s, now=%s, is_past=%s%n",
                        rs.getString("discount_code"),
                        rs.getTimestamp("start_date"),
                        rs.getTimestamp("current_time"),
                        rs.getBoolean("is_past"));
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error setting discount dates: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
