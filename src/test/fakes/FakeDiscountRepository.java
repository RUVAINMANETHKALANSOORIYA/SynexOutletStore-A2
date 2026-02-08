package fakes;

import domain.manager.Discount;
import ports.out.DiscountRepository;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class FakeDiscountRepository implements DiscountRepository {
    private final Map<Integer, Discount> discounts = new HashMap<>();
    private int nextId = 1;

    @Override
    public Discount createDiscount(Discount discount) {
        int id = discount.discountId() != null ? discount.discountId() : nextId++;
        Discount saved = new Discount(
            id, discount.discountCode(), discount.description(), discount.discountType(),
            discount.discountValue(), discount.minPurchaseAmount(), discount.maxDiscountAmount(),
            discount.startDate(), discount.endDate(), discount.isActive(),
            discount.usageLimit(), discount.timesUsed(), discount.createdBy(),
            discount.createdAt() != null ? discount.createdAt() : java.time.LocalDateTime.now(),
            java.time.LocalDateTime.now(), discount.itemCode(), discount.category()
        );
        discounts.put(id, saved);
        return saved;
    }

    @Override
    public Optional<Discount> findById(int discountId) {
        return Optional.ofNullable(discounts.get(discountId));
    }

    @Override
    public Optional<Discount> findByCode(String discountCode) {
        return discounts.values().stream()
                .filter(d -> d.discountCode().equals(discountCode))
                .findFirst();
    }

    @Override
    public List<Discount> findAll() {
        return new ArrayList<>(discounts.values());
    }

    @Override
    public void updateDiscount(Discount discount) {
        discounts.put(discount.discountId(), discount);
    }

    @Override
    public void deleteDiscount(int discountId) {
        discounts.remove(discountId);
    }

    @Override
    public List<Discount> findActiveDiscountsFIFO() {
        return discounts.values().stream()
                .filter(Discount::isActive)
                .sorted(Comparator.comparing(Discount::createdAt))
                .collect(Collectors.toList());
    }

    @Override
    public List<Discount> findValidDiscountsForAmount(BigDecimal purchaseAmount) {
        return discounts.values().stream()
                .filter(d -> d.isActive() && purchaseAmount.compareTo(d.minPurchaseAmount()) >= 0)
                .collect(Collectors.toList());
    }

    @Override
    public void incrementUsageCount(int discountId) {
        Discount d = discounts.get(discountId);
        if (d != null) {
            discounts.put(discountId, new Discount(
                d.discountId(), d.discountCode(), d.description(), d.discountType(),
                d.discountValue(), d.minPurchaseAmount(), d.maxDiscountAmount(),
                d.startDate(), d.endDate(), d.isActive(),
                d.usageLimit(), d.timesUsed() + 1, d.createdBy(),
                d.createdAt(), java.time.LocalDateTime.now(), d.itemCode(), d.category()
            ));
        }
    }

    @Override
    public void recordDiscountUsage(int orderId, int discountId, BigDecimal discountAmount) {
        incrementUsageCount(discountId);
    }

    @Override
    public List<Discount> findDiscountsByCreator(Long userId) {
        return discounts.values().stream()
                .filter(d -> userId.equals(d.createdBy()))
                .collect(Collectors.toList());
    }

    @Override
    public int getTotalActiveDiscounts() {
        return (int) discounts.values().stream().filter(Discount::isActive).count();
    }

    @Override
    public BigDecimal getTotalDiscountsSaved() {
        return BigDecimal.ZERO; // Simplified
    }
}
