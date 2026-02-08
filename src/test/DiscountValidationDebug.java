package test;

import application.DiscountServiceImpl;
import infrastructure.jdbc.JdbcDiscountRepository;
import java.time.LocalDateTime;

/**
 * Debug discount validation specifically
 */
public class DiscountValidationDebug {

    public static void main(String[] args) {
        System.out.println("=== DISCOUNT VALIDATION DEBUG ===\n");

        try {
            var discountRepo = new JdbcDiscountRepository();
            var discounts = discountRepo.findAll();

            LocalDateTime now = LocalDateTime.now();
            System.out.println("Current time: " + now);

            for (var discount : discounts) {
                System.out.printf("\nDiscount: %s%n", discount.discountCode());
                System.out.printf("- Active: %s%n", discount.isActive());
                System.out.printf("- Start: %s%n", discount.startDate());
                System.out.printf("- End: %s%n", discount.endDate());
                System.out.printf("- Usage: %d/%s%n", discount.timesUsed(),
                    discount.usageLimit() != null ? discount.usageLimit().toString() : "unlimited");

                System.out.printf("- Now after start: %s%n", now.isAfter(discount.startDate()));
                System.out.printf("- Now before end: %s%n", now.isBefore(discount.endDate()));
                System.out.printf("- Usage limit OK: %s%n",
                    discount.usageLimit() == null || discount.timesUsed() < discount.usageLimit());

                System.out.printf("- Overall valid: %s%n", discount.isValid());
            }

        } catch (Exception e) {
            System.err.println("Error debugging discount validation: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
