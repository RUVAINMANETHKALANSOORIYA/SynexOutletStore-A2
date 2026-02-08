package test;

import infrastructure.jdbc.Db;
import java.sql.*;

/**
 * Direct database query test to verify data structure
 */
public class DatabaseDebug {
    
    public static void main(String[] args) {
        System.out.println("=== DATABASE DEBUG ===\n");
        
        try (Connection conn = Db.get()) {
            
            // Check items table structure
            System.out.println("ITEMS TABLE STRUCTURE:");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("DESCRIBE items")) {
                while (rs.next()) {
                    System.out.printf("- %s: %s%n", rs.getString("Field"), rs.getString("Type"));
                }
            }
            
            // Check items data
            System.out.println("\nITEMS TABLE DATA:");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT item_code, name, category FROM items WHERE item_code IN ('LAPTOP001', 'SHIRT001', 'MOUSE001', 'PHONE001')")) {
                while (rs.next()) {
                    System.out.printf("- %s: %s (category: %s)%n", 
                        rs.getString("item_code"), 
                        rs.getString("name"),
                        rs.getString("category"));
                }
            }
            
            // Check discounts table structure
            System.out.println("\nDISCOUNTS TABLE STRUCTURE:");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("DESCRIBE discounts")) {
                while (rs.next()) {
                    System.out.printf("- %s: %s%n", rs.getString("Field"), rs.getString("Type"));
                }
            }
            
            // Check discount data
            System.out.println("\nDISCOUNTS TABLE DATA:");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT discount_code, item_code, category, start_date, end_date, is_active FROM discounts")) {
                while (rs.next()) {
                    System.out.printf("- %s: item=%s, category=%s, start=%s, end=%s, active=%s%n", 
                        rs.getString("discount_code"),
                        rs.getString("item_code"),
                        rs.getString("category"),
                        rs.getTimestamp("start_date"),
                        rs.getTimestamp("end_date"),
                        rs.getBoolean("is_active"));
                }
            }
            
        } catch (Exception e) {
            System.err.println("Database debug error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
