package test;

import application.DiscountServiceImpl;
import domain.cart.Cart;
import domain.cart.CartItem;
import infrastructure.jdbc.JdbcDiscountRepository;
import infrastructure.jdbc.JdbcItemRepository;
import java.math.BigDecimal;

/**
 * Comprehensive working demonstration of item-specific discounts
 */
public class ComprehensiveDiscountDemo {
    
    public static void main(String[] args) {
        System.out.println("=== COMPREHENSIVE ITEM-SPECIFIC DISCOUNT DEMONSTRATION ===\n");
        
        try {
            // Create test cart with various items
            Cart originalCart = new Cart();
            originalCart.add("LAPTOP001", "Gaming Laptop", 150000.0, 1);     // Should get 20% off (LAPTOP20)
            originalCart.add("SHIRT001", "Cotton T-Shirt", 2500.0, 3);       // Should get 15% off (CLOTHES15)
            originalCart.add("MOUSE001", "Wireless Mouse", 3500.0, 1);       // Should get 500 off (TECH500)
            originalCart.add("PHONE001", "Smartphone", 85000.0, 1);          // Should get 500 off (TECH500)
            
            System.out.println("STEP 1: ORIGINAL CART");
            displayOriginalCart(originalCart);
            
            // Initialize services
            var discountService = new DiscountServiceImpl(new JdbcDiscountRepository());
            var itemRepo = new JdbcItemRepository();
            
            System.out.println("\nSTEP 2: ACTIVE DISCOUNTS IN SYSTEM");
            var activeDiscounts = discountService.getActiveDiscounts();
            for (var discount : activeDiscounts) {
                System.out.printf("- %s: %s %.2f%% (applies to: %s)%n",
                    discount.discountCode(),
                    discount.discountType().name(),
                    discount.discountValue().doubleValue(),
                    getDiscountTarget(discount));
            }
            
            System.out.println("\nSTEP 3: APPLYING ITEM-SPECIFIC DISCOUNTS");
            
            // Manual discount application to demonstrate the logic
            Cart discountedCart = new Cart();
            BigDecimal totalSavings = BigDecimal.ZERO;
            
            for (var item : originalCart.items()) {
                var itemOpt = itemRepo.findByCode(item.itemCode());
                if (itemOpt.isEmpty()) {
                    System.out.printf("⚠️  Item %s not found in database%n", item.itemCode());
                    discountedCart.add(item.itemCode(), item.name(), item.unitPrice(), item.qty());
                    continue;
                }
                
                var storeItem = itemOpt.get();
                System.out.printf("\nProcessing: %s (category: %s)%n", 
                    item.name(), storeItem.category());
                
                // Find best discount for this item
                BigDecimal bestDiscountAmount = BigDecimal.ZERO;
                String bestDiscountCode = null;
                
                for (var discount : activeDiscounts) {
                    if (discount.isValid() && discount.appliesTo(item.itemCode(), storeItem.category())) {
                        BigDecimal discountAmount = calculateManualDiscount(discount, item);
                        System.out.printf("  - %s applicable: LKR %.2f savings%n", 
                            discount.discountCode(), discountAmount.doubleValue());
                        
                        if (discountAmount.compareTo(bestDiscountAmount) > 0) {
                            bestDiscountAmount = discountAmount;
                            bestDiscountCode = discount.discountCode();
                        }
                    }
                }
                
                if (bestDiscountAmount.compareTo(BigDecimal.ZERO) > 0) {
                    System.out.printf("  ✅ Best discount: %s saves LKR %.2f%n", 
                        bestDiscountCode, bestDiscountAmount.doubleValue());
                    discountedCart.addWithDiscount(item.itemCode(), item.name(), item.unitPrice(),
                                                  item.qty(), bestDiscountAmount, bestDiscountCode);
                    totalSavings = totalSavings.add(bestDiscountAmount);
                } else {
                    System.out.println("  ❌ No applicable discounts");
                    discountedCart.add(item.itemCode(), item.name(), item.unitPrice(), item.qty());
                }
            }
            
            System.out.println("\nSTEP 4: FINAL DISCOUNTED CART");
            displayDiscountedCart(discountedCart, totalSavings);
            
            System.out.println("\n=== SUMMARY ===");
            System.out.printf("Original Total: LKR %.2f%n", calculateOriginalTotal(originalCart).doubleValue());
            System.out.printf("Total Savings:  LKR %.2f%n", totalSavings.doubleValue());
            System.out.printf("Final Total:    LKR %.2f%n",
                (calculateOriginalTotal(originalCart).subtract(totalSavings)).doubleValue());
            
            double savingsPercentage = totalSavings.doubleValue() / calculateOriginalTotal(originalCart).doubleValue() * 100;
            System.out.printf("Savings Rate:   %.1f%%%n", savingsPercentage);
            
        } catch (Exception e) {
            System.err.println("Error in comprehensive demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void displayOriginalCart(Cart cart) {
        System.out.printf("%-12s %-20s %-5s %-12s %-12s%n", 
            "Code", "Name", "Qty", "Unit Price", "Line Total");
        System.out.println("-".repeat(70));
        
        for (CartItem item : cart.items()) {
            BigDecimal lineTotal = new BigDecimal(item.unitPrice()).multiply(new BigDecimal(item.qty()));
            System.out.printf("%-12s %-20s %-5d LKR %8.2f LKR %8.2f%n",
                item.itemCode(), item.name(), item.qty(), 
                item.unitPrice(), lineTotal.doubleValue());
        }
    }
    
    private static void displayDiscountedCart(Cart cart, BigDecimal totalSavings) {
        System.out.printf("%-12s %-20s %-5s %-12s %-15s %-12s%n", 
            "Code", "Name", "Qty", "Final Price", "Discount", "Line Total");
        System.out.println("-".repeat(85));
        
        for (CartItem item : cart.items()) {
            String discountInfo = item.hasDiscount() ? 
                String.format("-%.2f (%s)", item.discountAmount().doubleValue(), item.appliedDiscountCode()) : 
                "None";
            
            System.out.printf("%-12s %-20s %-5d LKR %8.2f %-15s LKR %8.2f%n",
                item.itemCode(), item.name(), item.qty(),
                item.discountedUnitPrice().doubleValue(), discountInfo,
                item.discountedLineTotal().doubleValue());
        }
    }
    
    private static BigDecimal calculateOriginalTotal(Cart cart) {
        BigDecimal total = BigDecimal.ZERO;
        for (var item : cart.items()) {
            total = total.add(new BigDecimal(item.unitPrice()).multiply(new BigDecimal(item.qty())));
        }
        return total;
    }
    
    private static String getDiscountTarget(domain.manager.Discount discount) {
        if (discount.itemCode() != null) {
            return "item " + discount.itemCode();
        } else if (discount.category() != null) {
            return "category " + discount.category();
        } else {
            return "all items";
        }
    }
    
    private static BigDecimal calculateManualDiscount(domain.manager.Discount discount, CartItem item) {
        BigDecimal lineTotal = new BigDecimal(item.unitPrice()).multiply(new BigDecimal(item.qty()));
        
        if (discount.discountType().name().equals("PERCENTAGE")) {
            return lineTotal.multiply(discount.discountValue()).divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
        } else {
            return discount.discountValue().min(lineTotal); // Can't discount more than the item costs
        }
    }
}
