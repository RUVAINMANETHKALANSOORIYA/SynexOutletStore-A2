package test;

import infrastructure.jdbc.Db;
import java.sql.*;

/**
 * Fix discount start dates to be valid now
 */
public class FixDiscountDates {
    
    public static void main(String[] args) {
        System.out.println("Fixing discount start dates...");
        
        try (Connection conn = Db.get()) {
            
            // Update all discounts to start now
            String sql = "UPDATE discounts SET start_date = NOW() WHERE discount_code IN ('LAPTOP20', 'CLOTHES15', 'TECH500')";
            
            try (Statement stmt = conn.createStatement()) {
                int updated = stmt.executeUpdate(sql);
                System.out.println("✓ Updated " + updated + " discount start dates to current time");
            }
            
            // Verify the fix
            String verifySql = "SELECT discount_code, start_date, end_date, NOW() as current_time FROM discounts WHERE discount_code IN ('LAPTOP20', 'CLOTHES15', 'TECH500')";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(verifySql)) {
                
                System.out.println("\nVerification:");
                while (rs.next()) {
                    System.out.printf("- %s: start=%s, end=%s, now=%s%n",
                        rs.getString("discount_code"),
                        rs.getTimestamp("start_date"),
                        rs.getTimestamp("end_date"),
                        rs.getTimestamp("current_time"));
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error fixing discount dates: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
