package test;

import application.DiscountServiceImpl;
import domain.cart.Cart;
import infrastructure.jdbc.JdbcDiscountRepository;
import infrastructure.jdbc.JdbcItemRepository;

/**
 * Step by step debug of discount application
 */
public class StepByStepDiscountDebug {
    
    public static void main(String[] args) {
        System.out.println("=== STEP-BY-STEP DISCOUNT APPLICATION DEBUG ===\n");
        
        try {
            // Create test cart
            Cart cart = new Cart();
            cart.add("LAPTOP001", "Gaming Laptop", 150000.0, 1);
            
            var discountService = new DiscountServiceImpl(new JdbcDiscountRepository());
            var itemRepo = new JdbcItemRepository();
            
            System.out.println("1. Original cart item:");
            for (var item : cart.items()) {
                System.out.printf("   - %s: %s × %d @ LKR %.2f%n", 
                    item.itemCode(), item.name(), item.qty(), item.unitPrice());
            }
            
            System.out.println("\n2. Getting active discounts...");
            var activeDiscounts = discountService.getActiveDiscounts();
            System.out.println("   Found " + activeDiscounts.size() + " active discounts");
            
            for (var discount : activeDiscounts) {
                System.out.printf("   - %s: valid=%s, item=%s, category=%s%n", 
                    discount.discountCode(), discount.isValid(), 
                    discount.itemCode(), discount.category());
            }
            
            System.out.println("\n3. Checking item details...");
            var itemOpt = itemRepo.findByCode("LAPTOP001");
            if (itemOpt.isPresent()) {
                var item = itemOpt.get();
                System.out.printf("   - LAPTOP001 found: %s (category: %s)%n", 
                    item.name(), item.category());
                
                System.out.println("\n4. Testing discount matching...");
                for (var discount : activeDiscounts) {
                    boolean applies = discount.appliesTo("LAPTOP001", item.category());
                    System.out.printf("   - %s applies to LAPTOP001: %s%n", 
                        discount.discountCode(), applies);
                    
                    if (applies) {
                        var discountAmount = discount.calculateItemDiscount(
                            java.math.BigDecimal.valueOf(150000.0), 1);
                        System.out.printf("     Calculated discount amount: LKR %.2f%n", 
                            discountAmount.doubleValue());
                    }
                }
            } else {
                System.out.println("   - LAPTOP001 not found!");
            }
            
            System.out.println("\n5. Applying discounts to cart...");
            Cart discountedCart = discountService.applyItemDiscountsToCart(cart, itemRepo);
            
            System.out.println("\n6. Final cart:");
            for (var item : discountedCart.items()) {
                System.out.printf("   - %s: discount=LKR %.2f, code=%s%n", 
                    item.itemCode(), 
                    item.discountAmount() != null ? item.discountAmount().doubleValue() : 0.0,
                    item.appliedDiscountCode());
            }
            
            System.out.printf("\nTotal discount amount: LKR %.2f%n", 
                discountedCart.totalDiscountAmount().doubleValue());
            
        } catch (Exception e) {
            System.err.println("Error in step-by-step debug: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
