package test;

import application.DiscountServiceImpl;
import infrastructure.jdbc.JdbcDiscountRepository;
import java.math.BigDecimal;

/**
 * Debug the calculateItemDiscount method specifically
 */
public class CalculateDiscountDebug {
    
    public static void main(String[] args) {
        System.out.println("=== CALCULATE DISCOUNT DEBUG ===\n");
        
        try {
            var discountRepo = new JdbcDiscountRepository();
            var discounts = discountRepo.findAll();
            
            for (var discount : discounts) {
                System.out.printf("Testing discount: %s%n", discount.discountCode());
                System.out.printf("- Type: %s%n", discount.discountType());
                System.out.printf("- Value: %.2f%n", discount.discountValue().doubleValue());
                System.out.printf("- Min purchase: %.2f%n", discount.minPurchaseAmount().doubleValue());
                System.out.printf("- Max discount: %s%n", 
                    discount.maxDiscountAmount() != null ? discount.maxDiscountAmount().toString() : "unlimited");
                System.out.printf("- Valid: %s%n", discount.isValid());
                
                if (discount.isValid()) {
                    BigDecimal itemPrice = new BigDecimal("150000.00");
                    int quantity = 1;
                    BigDecimal result = discount.calculateItemDiscount(itemPrice, quantity);
                    
                    System.out.printf("- Discount for LKR 150,000 × 1: LKR %.2f%n", result.doubleValue());
                    
                    // Test the logic step by step
                    BigDecimal lineTotal = itemPrice.multiply(new BigDecimal(quantity));
                    System.out.printf("- Line total: LKR %.2f%n", lineTotal.doubleValue());
                    
                    if (discount.discountType().name().equals("PERCENTAGE")) {
                        BigDecimal discountAmount = lineTotal.multiply(discount.discountValue())
                            .divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
                        System.out.printf("- Percentage calculation: %.2f%% of %.2f = %.2f%n", 
                            discount.discountValue().doubleValue(), lineTotal.doubleValue(), discountAmount.doubleValue());
                    } else {
                        System.out.printf("- Fixed amount: LKR %.2f%n", discount.discountValue().doubleValue());
                    }
                } else {
                    System.out.println("- Discount not valid, skipping calculation");
                }
                
                System.out.println();
            }
            
        } catch (Exception e) {
            System.err.println("Error debugging discount calculation: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
