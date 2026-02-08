package test;

import application.DiscountServiceImpl;
import infrastructure.jdbc.JdbcDiscountRepository;
import infrastructure.jdbc.JdbcItemRepository;

/**
 * Debug discount system issues
 */
public class DebugDiscountSystem {

    public static void main(String[] args) {
        System.out.println("=== DEBUGGING DISCOUNT SYSTEM ===\n");

        try {
            // Test discount repository
            var discountRepo = new JdbcDiscountRepository();
            var discounts = discountRepo.findAll();

            System.out.println("DISCOUNTS IN DATABASE:");
            if (discounts.isEmpty()) {
                System.out.println("No discounts found!");
            } else {
                for (var discount : discounts) {
                    System.out.printf("- %s: %s %.2f%% (item: %s, category: %s)%n",
                        discount.discountCode(),
                        discount.description(),
                        discount.discountValue().doubleValue(),
                        discount.itemCode() != null ? discount.itemCode() : "all",
                        discount.category() != null ? discount.category() : "all");
                }
            }

            System.out.println("\nITEMS IN DATABASE:");
            var itemRepo = new JdbcItemRepository();
            var laptop = itemRepo.findByCode("LAPTOP001");
            var shirt = itemRepo.findByCode("SHIRT001");
            var mouse = itemRepo.findByCode("MOUSE001");
            var phone = itemRepo.findByCode("PHONE001");

            System.out.printf("LAPTOP001: %s (category: %s)%n",
                laptop.isPresent() ? "Found" : "Not found",
                laptop.isPresent() ? laptop.get().category() : "N/A");

            System.out.printf("SHIRT001: %s (category: %s)%n",
                shirt.isPresent() ? "Found" : "Not found",
                shirt.isPresent() ? shirt.get().category() : "N/A");

            System.out.printf("MOUSE001: %s (category: %s)%n",
                mouse.isPresent() ? "Found" : "Not found",
                mouse.isPresent() ? mouse.get().category() : "N/A");

            System.out.printf("PHONE001: %s (category: %s)%n",
                phone.isPresent() ? "Found" : "Not found",
                phone.isPresent() ? phone.get().category() : "N/A");

            // Test active discounts
            System.out.println("\nACTIVE DISCOUNTS:");
            var discountService = new DiscountServiceImpl(discountRepo);
            var activeDiscounts = discountService.getActiveDiscounts();

            if (activeDiscounts.isEmpty()) {
                System.out.println("No active discounts found!");
            } else {
                for (var discount : activeDiscounts) {
                    System.out.printf("- %s: Active=%s, Valid=%s%n",
                        discount.discountCode(),
                        discount.isActive(),
                        discount.isValid());
                }
            }

        } catch (Exception e) {
            System.err.println("Error debugging discount system: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
