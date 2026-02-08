package ports.in;

import domain.manager.Discount;
import java.math.BigDecimal;
import java.util.List;

public interface DiscountService {

    // CRUD operations
    Discount createDiscount(Discount discount);
    Discount updateDiscount(Discount discount);
    void deleteDiscount(int discountId);
    void toggleDiscountStatus(int discountId);

    // Query operations
    List<Discount> getAllDiscounts();
    List<Discount> getActiveDiscounts();
    Discount getDiscountById(int discountId);
    Discount getDiscountByCode(String discountCode);

    // FIFO discount application
    DiscountResult applyBestDiscount(BigDecimal subtotal, String discountCode);
    DiscountResult applyBestDiscountAuto(BigDecimal subtotal);

    // Analytics
    int getTotalActiveDiscounts();
    BigDecimal getTotalDiscountsSaved();

    // Result class for discount application
    record DiscountResult(
        Discount appliedDiscount,
        BigDecimal discountAmount,
        BigDecimal finalAmount,
        String message
    ) {
        public static DiscountResult noDiscount(BigDecimal originalAmount) {
            return new DiscountResult(null, BigDecimal.ZERO, originalAmount, "No discount applied");
        }

        public static DiscountResult success(Discount discount, BigDecimal discountAmount, BigDecimal finalAmount) {
            return new DiscountResult(discount, discountAmount, finalAmount,
                String.format("Discount '%s' applied: LKR %.2f off",
                    discount.discountCode(), discountAmount.doubleValue()));
        }

        public static DiscountResult error(BigDecimal originalAmount, String errorMessage) {
            return new DiscountResult(null, BigDecimal.ZERO, originalAmount, errorMessage);
        }
    }
}
