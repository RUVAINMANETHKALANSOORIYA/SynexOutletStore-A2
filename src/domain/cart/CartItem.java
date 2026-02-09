package domain.cart;

import java.math.BigDecimal;

public record CartItem(String itemCode, String name, double unitPrice, int qty,
                      BigDecimal discountAmount, String appliedDiscountCode) {

    // Constructor for items without discount (backward compatibility)
    public CartItem(String itemCode, String name, double unitPrice, int qty) {
        this(itemCode, name, unitPrice, qty, BigDecimal.ZERO, null);
    }

    public double lineTotal() {
        return unitPrice * qty;
    }

    public BigDecimal discountedLineTotal() {
        BigDecimal originalTotal = new BigDecimal(unitPrice).multiply(new BigDecimal(qty));
        return originalTotal.subtract(discountAmount != null ? discountAmount : BigDecimal.ZERO);
    }

    public BigDecimal discountPerUnit() {
        if (discountAmount == null || qty == 0) {
            return BigDecimal.ZERO;
        }
        return discountAmount.divide(new BigDecimal(qty), 2, java.math.RoundingMode.HALF_UP);
    }

    // ✅ NEW: Get discounted unit price
    public BigDecimal discountedUnitPrice() {
        return new BigDecimal(unitPrice).subtract(discountPerUnit());
    }

    // ✅ NEW: Check if item has discount
    public boolean hasDiscount() {
        return discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0;
    }
}
