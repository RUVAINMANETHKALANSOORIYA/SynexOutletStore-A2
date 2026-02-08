package test;

import infrastructure.jdbc.Db;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Setup test data for item-specific discounts
 */
public class SetupDiscountTestData {

    public static void main(String[] args) {
        System.out.println("Setting up test data for item-specific discounts...");

        try (Connection conn = Db.get();
             Statement stmt = conn.createStatement()) {

            // 1. Add category field to items if not exists
            try {
                stmt.executeUpdate("ALTER TABLE items ADD COLUMN category VARCHAR(50) NULL");
                System.out.println("✓ Added category column to items table");
            } catch (Exception e) {
                System.out.println("ℹ Category column already exists in items table");
            }

            // 2. Add item_code and category fields to discounts if not exist
            try {
                stmt.executeUpdate("ALTER TABLE discounts ADD COLUMN item_code VARCHAR(20) NULL");
                System.out.println("✓ Added item_code column to discounts table");
            } catch (Exception e) {
                System.out.println("ℹ item_code column already exists in discounts table");
            }

            try {
                stmt.executeUpdate("ALTER TABLE discounts ADD COLUMN category VARCHAR(50) NULL");
                System.out.println("✓ Added category column to discounts table");
            } catch (Exception e) {
                System.out.println("ℹ category column already exists in discounts table");
            }

            // 3. Update some items with categories
            stmt.executeUpdate("UPDATE items SET category = 'Electronics' WHERE item_code IN ('LAPTOP001', 'PHONE001', 'MOUSE001')");
            stmt.executeUpdate("UPDATE items SET category = 'Clothing' WHERE item_code LIKE 'SHIRT%' OR item_code LIKE 'PANTS%'");
            System.out.println("✓ Updated items with categories");

            // 4. Insert sample item-specific discounts
            stmt.executeUpdate("""
                INSERT INTO discounts (discount_code, description, discount_type, discount_value, 
                                     min_purchase_amount, max_discount_amount, start_date, end_date, 
                                     is_active, usage_limit, times_used, created_by, item_code, category)
                VALUES 
                ('LAPTOP20', '20% off Gaming Laptop', 'PERCENTAGE', 20.00, 0.00, 50000.00, 
                 NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), TRUE, 100, 0, 1, 'LAPTOP001', NULL)
                ON DUPLICATE KEY UPDATE discount_code = discount_code
                """);

            stmt.executeUpdate("""
                INSERT INTO discounts (discount_code, description, discount_type, discount_value, 
                                     min_purchase_amount, max_discount_amount, start_date, end_date, 
                                     is_active, usage_limit, times_used, created_by, item_code, category)
                VALUES 
                ('CLOTHES15', '15% off all clothing', 'PERCENTAGE', 15.00, 1000.00, 2000.00,
                 NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), TRUE, 200, 0, 1, NULL, 'Clothing')
                ON DUPLICATE KEY UPDATE discount_code = discount_code
                """);

            stmt.executeUpdate("""
                INSERT INTO discounts (discount_code, description, discount_type, discount_value, 
                                     min_purchase_amount, max_discount_amount, start_date, end_date, 
                                     is_active, usage_limit, times_used, created_by, item_code, category)
                VALUES 
                ('TECH500', 'LKR 500 off electronics', 'FIXED_AMOUNT', 500.00, 2000.00, 500.00,
                 NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), TRUE, 50, 0, 1, NULL, 'Electronics')
                ON DUPLICATE KEY UPDATE discount_code = discount_code
                """);

            System.out.println("✓ Created sample item-specific discounts");
            System.out.println("\nSample discounts created:");
            System.out.println("- LAPTOP20: 20% off Gaming Laptop (item-specific)");
            System.out.println("- CLOTHES15: 15% off all clothing (category-based)");
            System.out.println("- TECH500: LKR 500 off electronics (category-based)");
            System.out.println("\nDatabase setup complete! Run the test again.");

        } catch (Exception e) {
            System.err.println("Error setting up test data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
