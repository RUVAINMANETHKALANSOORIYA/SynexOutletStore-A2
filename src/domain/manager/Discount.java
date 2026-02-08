package domain.manager;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Discount(
    Integer discountId,
    String discountCode,
    String description,
    DiscountType discountType,
    BigDecimal discountValue,
    BigDecimal minPurchaseAmount,
    BigDecimal maxDiscountAmount,
    LocalDateTime startDate,
    LocalDateTime endDate,
    Boolean isActive,
    Integer usageLimit,
    Integer timesUsed,
    Long createdBy,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String itemCode,
    String category
) {

    public enum DiscountType {
        PERCENTAGE, FIXED_AMOUNT
    }

    // Constructor for creating new discounts
    public Discount(String discountCode, String description, DiscountType discountType,
                   BigDecimal discountValue, BigDecimal minPurchaseAmount,
                   BigDecimal maxDiscountAmount, LocalDateTime startDate,
                   LocalDateTime endDate, Integer usageLimit, Long createdBy,
                   String itemCode, String category) {
        this(null, discountCode, description, discountType, discountValue,
             minPurchaseAmount, maxDiscountAmount, startDate, endDate, true,
             usageLimit, 0, createdBy, null, null, itemCode, category);
    }

    // Constructor for order-level discounts (backward compatibility)
    public Discount(String discountCode, String description, DiscountType discountType,
                   BigDecimal discountValue, BigDecimal minPurchaseAmount,
                   BigDecimal maxDiscountAmount, LocalDateTime startDate,
                   LocalDateTime endDate, Integer usageLimit, Long createdBy) {
        this(discountCode, description, discountType, discountValue, minPurchaseAmount,
             maxDiscountAmount, startDate, endDate, usageLimit, createdBy, null, null);
    }

    // Business logic methods
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return isActive &&
               (now.isAfter(startDate) || now.isEqual(startDate)) &&
               now.isBefore(endDate) &&
               (usageLimit == null || timesUsed < usageLimit);
    }

    public BigDecimal calculateDiscount(BigDecimal subtotal) {
        if (!isValid() || subtotal.compareTo(minPurchaseAmount) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discountAmount;
        if (discountType == DiscountType.PERCENTAGE) {
            discountAmount = subtotal.multiply(discountValue).divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
        } else {
            discountAmount = discountValue;
        }

        // Apply max discount cap if specified
        if (maxDiscountAmount != null && discountAmount.compareTo(maxDiscountAmount) > 0) {
            discountAmount = maxDiscountAmount;
        }

        return discountAmount;
    }

    public boolean canBeUsed() {
        return isValid() && (usageLimit == null || timesUsed < usageLimit);
    }

    public int getRemainingUses() {
        if (usageLimit == null) return Integer.MAX_VALUE;
        return Math.max(0, usageLimit - timesUsed);
    }

    public String getUsageStatus() {
        if (usageLimit == null) return "Unlimited";
        return timesUsed + "/" + usageLimit;
    }

    // ✅ NEW: Check if discount applies to specific item
    public boolean appliesTo(String itemCode, String itemCategory) {
        // If both itemCode and category are null, it's an order-level discount
        if (this.itemCode == null && this.category == null) {
            return true;
        }

        // Check specific item code match
        if (this.itemCode != null) {
            return this.itemCode.equals(itemCode);
        }

        // Check category match
        if (this.category != null) {
            return this.category.equals(itemCategory);
        }

        return false;
    }


    public BigDecimal calculateItemDiscount(BigDecimal itemPrice, int quantity) {
        if (!isValid()) {
            return BigDecimal.ZERO;
        }

        BigDecimal lineTotal = itemPrice.multiply(new BigDecimal(quantity));

        BigDecimal discountAmount;
        if (discountType == DiscountType.PERCENTAGE) {
            discountAmount = lineTotal.multiply(discountValue).divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
        } else {
            // For fixed amount, apply to line total
            discountAmount = discountValue;
        }

        // Apply max discount cap if specified
        if (maxDiscountAmount != null && discountAmount.compareTo(maxDiscountAmount) > 0) {
            discountAmount = maxDiscountAmount;
        }

        return discountAmount;
    }

    public boolean isItemSpecific() {
        return itemCode != null || category != null;
    }
}
