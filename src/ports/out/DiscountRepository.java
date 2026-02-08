package ports.out;

import domain.manager.Discount;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface DiscountRepository {

    // CRUD Operations
    Discount createDiscount(Discount discount);
    Optional<Discount> findById(int discountId);
    Optional<Discount> findByCode(String discountCode);
    List<Discount> findAll();
    void updateDiscount(Discount discount);
    void deleteDiscount(int discountId);

    // FIFO-based discount queries
    List<Discount> findActiveDiscountsFIFO();
    List<Discount> findValidDiscountsForAmount(BigDecimal purchaseAmount);

    // Usage tracking
    void incrementUsageCount(int discountId);
    void recordDiscountUsage(int orderId, int discountId, BigDecimal discountAmount);

    // Manager dashboard queries
    List<Discount> findDiscountsByCreator(Long userId);
    int getTotalActiveDiscounts();
    BigDecimal getTotalDiscountsSaved();
}
