package test;

import infrastructure.jdbc.Db;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Create test items for discount testing
 */
public class CreateTestItems {

    public static void main(String[] args) {
        System.out.println("Creating test items for discount testing...");

        try (Connection conn = Db.get()) {

            String sql = "INSERT INTO items (item_code, name, price, is_active, category) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE name = VALUES(name), category = VALUES(category)";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                // Gaming Laptop
                stmt.setString(1, "LAPTOP001");
                stmt.setString(2, "Gaming Laptop");
                stmt.setBigDecimal(3, new BigDecimal("150000.00"));
                stmt.setBoolean(4, true);
                stmt.setString(5, "Electronics");
                stmt.addBatch();

                // Cotton T-Shirt
                stmt.setString(1, "SHIRT001");
                stmt.setString(2, "Cotton T-Shirt");
                stmt.setBigDecimal(3, new BigDecimal("2500.00"));
                stmt.setBoolean(4, true);
                stmt.setString(5, "Clothing");
                stmt.addBatch();

                // Wireless Mouse
                stmt.setString(1, "MOUSE001");
                stmt.setString(2, "Wireless Mouse");
                stmt.setBigDecimal(3, new BigDecimal("3500.00"));
                stmt.setBoolean(4, true);
                stmt.setString(5, "Electronics");
                stmt.addBatch();

                // Smartphone
                stmt.setString(1, "PHONE001");
                stmt.setString(2, "Smartphone");
                stmt.setBigDecimal(3, new BigDecimal("85000.00"));
                stmt.setBoolean(4, true);
                stmt.setString(5, "Electronics");
                stmt.addBatch();

                int[] results = stmt.executeBatch();

                System.out.println("✓ Created " + results.length + " test items:");
                System.out.println("- LAPTOP001: Gaming Laptop (Electronics)");
                System.out.println("- SHIRT001: Cotton T-Shirt (Clothing)");
                System.out.println("- MOUSE001: Wireless Mouse (Electronics)");
                System.out.println("- PHONE001: Smartphone (Electronics)");
            }

        } catch (Exception e) {
            System.err.println("Error creating test items: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
