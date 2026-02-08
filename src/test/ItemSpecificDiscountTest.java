package test;

import application.DiscountServiceImpl;
import domain.cart.Cart;
import domain.cart.CartItem;
import domain.manager.Discount;
import domain.store.Item;
import infrastructure.jdbc.JdbcDiscountRepository;
import infrastructure.jdbc.JdbcItemRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Test class to demonstrate item-specific discount functionality
 */
public class ItemSpecificDiscountTest {
    
    public static void main(String[] args) {
        System.out.println("=== ITEM-SPECIFIC DISCOUNT SYSTEM TEST ===\n");
        
        // Create test cart
        Cart cart = new Cart();
        cart.add("LAPTOP001", "Gaming Laptop", 150000.0, 1);
        cart.add("SHIRT001", "Cotton T-Shirt", 2500.0, 2);
        cart.add("MOUSE001", "Wireless Mouse", 3500.0, 1);
        cart.add("PHONE001", "Smartphone", 85000.0, 1);
        
        System.out.println("ORIGINAL CART:");
        displayCart(cart);
        
        // Apply item-specific discounts
        try {
            var discountService = new DiscountServiceImpl(new JdbcDiscountRepository());
            var itemRepo = new JdbcItemRepository();
            
            // Apply discounts
            Cart discountedCart = discountService.applyItemDiscountsToCart(cart, itemRepo);
            
            System.out.println("\nDISCOUNTED CART:");
            displayDiscountedCart(discountedCart);
            
            // Show available discounts
            var availableDiscounts = discountService.getAvailableItemDiscounts(cart, itemRepo);
            System.out.println("\nAVAILABLE DISCOUNTS:");
            for (var discountInfo : availableDiscounts) {
                System.out.printf("- %s (%s): %s - Save LKR %.2f%n", 
                    discountInfo.itemName(), 
                    discountInfo.itemCode(),
                    discountInfo.discountCode(), 
                    discountInfo.discountAmount());
            }
            
        } catch (Exception e) {
            System.err.println("Error testing discounts: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void displayCart(Cart cart) {
        System.out.printf("%-12s %-20s %-5s %-12s %-12s%n", 
            "Code", "Name", "Qty", "Unit Price", "Line Total");
        System.out.println("-".repeat(70));
        
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cart.items()) {
            BigDecimal lineTotal = new BigDecimal(item.unitPrice())
                .multiply(new BigDecimal(item.qty()));
            total = total.add(lineTotal);
            
            System.out.printf("%-12s %-20s %-5d LKR %8.2f LKR %8.2f%n",
                item.itemCode(), item.name(), item.qty(), 
                item.unitPrice(), lineTotal.doubleValue());
        }
        
        System.out.println("-".repeat(70));
        System.out.printf("TOTAL: LKR %.2f%n", total.doubleValue());
    }
    
    private static void displayDiscountedCart(Cart cart) {
        System.out.printf("%-12s %-20s %-5s %-12s %-12s %-12s %-15s%n", 
            "Code", "Name", "Qty", "Unit Price", "Discount", "Final Price", "Line Total");
        System.out.println("-".repeat(105));
        
        BigDecimal originalTotal = BigDecimal.ZERO;
        BigDecimal finalTotal = BigDecimal.ZERO;
        BigDecimal totalSavings = BigDecimal.ZERO;
        
        for (CartItem item : cart.items()) {
            BigDecimal originalLineTotal = new BigDecimal(item.unitPrice())
                .multiply(new BigDecimal(item.qty()));
            BigDecimal discountedLineTotal = item.discountedLineTotal();
            BigDecimal itemDiscount = item.discountAmount() != null ? item.discountAmount() : BigDecimal.ZERO;
            
            originalTotal = originalTotal.add(originalLineTotal);
            finalTotal = finalTotal.add(discountedLineTotal);
            totalSavings = totalSavings.add(itemDiscount);
            
            String discountDisplay = itemDiscount.compareTo(BigDecimal.ZERO) > 0 ? 
                String.format("-%.2f (%s)", itemDiscount.doubleValue(), 
                    item.appliedDiscountCode() != null ? item.appliedDiscountCode() : "N/A") : 
                "-";
                
            BigDecimal finalUnitPrice = item.discountedUnitPrice();
            
            System.out.printf("%-12s %-20s %-5d LKR %8.2f %-12s LKR %8.2f LKR %8.2f%n",
                item.itemCode(), item.name(), item.qty(), 
                item.unitPrice(), discountDisplay,
                finalUnitPrice.doubleValue(), discountedLineTotal.doubleValue());
        }
        
        System.out.println("-".repeat(105));
        System.out.printf("ORIGINAL TOTAL: LKR %.2f%n", originalTotal.doubleValue());
        System.out.printf("TOTAL SAVINGS:  LKR %.2f%n", totalSavings.doubleValue());
        System.out.printf("FINAL TOTAL:    LKR %.2f%n", finalTotal.doubleValue());
    }
}
