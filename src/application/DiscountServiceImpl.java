package application;

import domain.manager.Discount;
import ports.in.DiscountService;
import ports.out.DiscountRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DiscountServiceImpl implements DiscountService {

    private final DiscountRepository discountRepository;

    public DiscountServiceImpl(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    @Override
    public Discount createDiscount(Discount discount) {
        // Validation
        validateDiscount(discount);

        // Check if discount code already exists
        if (discountRepository.findByCode(discount.discountCode()).isPresent()) {
            throw new IllegalArgumentException("Discount code already exists: " + discount.discountCode());
        }

        return discountRepository.createDiscount(discount);
    }

    @Override
    public Discount updateDiscount(Discount discount) {
        validateDiscount(discount);

        // Check if discount exists
        var existingDiscount = discountRepository.findById(discount.discountId())
            .orElseThrow(() -> new IllegalArgumentException("Discount not found: " + discount.discountId()));

        // Check if code is being changed and if new code already exists
        if (!existingDiscount.discountCode().equals(discount.discountCode())) {
            if (discountRepository.findByCode(discount.discountCode()).isPresent()) {
                throw new IllegalArgumentException("Discount code already exists: " + discount.discountCode());
            }
        }

        discountRepository.updateDiscount(discount);
        return discountRepository.findById(discount.discountId()).orElseThrow();
    }

    @Override
    public void deleteDiscount(int discountId) {
        if (discountRepository.findById(discountId).isEmpty()) {
            throw new IllegalArgumentException("Discount not found: " + discountId);
        }

        discountRepository.deleteDiscount(discountId);
    }

    @Override
    public void toggleDiscountStatus(int discountId) {
        var discount = discountRepository.findById(discountId)
            .orElseThrow(() -> new IllegalArgumentException("Discount not found: " + discountId));

        var updatedDiscount = new Discount(
            discount.discountId(),
            discount.discountCode(),
            discount.description(),
            discount.discountType(),
            discount.discountValue(),
            discount.minPurchaseAmount(),
            discount.maxDiscountAmount(),
            discount.startDate(),
            discount.endDate(),
            discount.isActive(),
            discount.usageLimit(),
            discount.timesUsed(),
            discount.createdBy(),
            discount.createdAt(),
            discount.updatedAt(),
            discount.itemCode(),
            discount.category()
        );

        discountRepository.updateDiscount(updatedDiscount);
    }

    @Override
    public List<Discount> getAllDiscounts() {
        return discountRepository.findAll();
    }

    @Override
    public List<Discount> getActiveDiscounts() {
        return discountRepository.findActiveDiscountsFIFO();
    }

    @Override
    public Discount getDiscountById(int discountId) {
        return discountRepository.findById(discountId)
            .orElseThrow(() -> new IllegalArgumentException("Discount not found: " + discountId));
    }

    @Override
    public Discount getDiscountByCode(String discountCode) {
        return discountRepository.findByCode(discountCode)
            .orElseThrow(() -> new IllegalArgumentException("Discount not found: " + discountCode));
    }

    @Override
    public DiscountResult applyBestDiscount(BigDecimal subtotal, String discountCode) {
        if (discountCode == null || discountCode.trim().isEmpty()) {
            return applyBestDiscountAuto(subtotal);
        }

        try {
            var discount = discountRepository.findByCode(discountCode.trim().toUpperCase())
                .orElse(null);

            if (discount == null) {
                return DiscountResult.error(subtotal, "Invalid discount code: " + discountCode);
            }

            if (!discount.isValid()) {
                String reason = getDiscountInvalidReason(discount);
                return DiscountResult.error(subtotal, "Discount '" + discountCode + "' is " + reason);
            }

            if (subtotal.compareTo(discount.minPurchaseAmount()) < 0) {
                return DiscountResult.error(subtotal,
                    String.format("Minimum purchase amount is LKR %.2f for discount '%s'",
                        discount.minPurchaseAmount().doubleValue(), discountCode));
            }

            BigDecimal discountAmount = discount.calculateDiscount(subtotal);
            BigDecimal finalAmount = subtotal.subtract(discountAmount);

            return DiscountResult.success(discount, discountAmount, finalAmount);

        } catch (Exception e) {
            return DiscountResult.error(subtotal, "Error applying discount: " + e.getMessage());
        }
    }

    @Override
    public DiscountResult applyBestDiscountAuto(BigDecimal subtotal) {
        try {
            List<Discount> validDiscounts = discountRepository.findValidDiscountsForAmount(subtotal);

            if (validDiscounts.isEmpty()) {
                return DiscountResult.noDiscount(subtotal);
            }

            // FIFO: Apply the first (oldest) valid discount
            Discount bestDiscount = null;
            BigDecimal maxDiscountAmount = BigDecimal.ZERO;

            for (Discount discount : validDiscounts) {
                BigDecimal discountAmount = discount.calculateDiscount(subtotal);
                if (discountAmount.compareTo(maxDiscountAmount) > 0) {
                    maxDiscountAmount = discountAmount;
                    bestDiscount = discount;
                }
            }

            if (bestDiscount != null && maxDiscountAmount.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal finalAmount = subtotal.subtract(maxDiscountAmount);
                return DiscountResult.success(bestDiscount, maxDiscountAmount, finalAmount);
            }

            return DiscountResult.noDiscount(subtotal);

        } catch (Exception e) {
            return DiscountResult.error(subtotal, "Error finding best discount: " + e.getMessage());
        }
    }

    @Override
    public int getTotalActiveDiscounts() {
        return discountRepository.getTotalActiveDiscounts();
    }

    @Override
    public BigDecimal getTotalDiscountsSaved() {
        return discountRepository.getTotalDiscountsSaved();
    }

    private void validateDiscount(Discount discount) {
        if (discount.discountCode() == null || discount.discountCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Discount code is required");
        }

        if (discount.description() == null || discount.description().trim().isEmpty()) {
            throw new IllegalArgumentException("Discount description is required");
        }

        if (discount.discountValue() == null || discount.discountValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Discount value must be positive");
        }

        if (discount.discountType() == Discount.DiscountType.PERCENTAGE) {
            if (discount.discountValue().compareTo(new BigDecimal("100")) > 0) {
                throw new IllegalArgumentException("Percentage discount cannot exceed 100%");
            }
        }

        if (discount.minPurchaseAmount() == null || discount.minPurchaseAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Minimum purchase amount cannot be negative");
        }

        if (discount.startDate() == null || discount.endDate() == null) {
            throw new IllegalArgumentException("Start date and end date are required");
        }

        if (!discount.startDate().isBefore(discount.endDate())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        if (discount.usageLimit() != null && discount.usageLimit() <= 0) {
            throw new IllegalArgumentException("Usage limit must be positive");
        }

        if (discount.maxDiscountAmount() != null && discount.maxDiscountAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Maximum discount amount must be positive");
        }
    }

    private String getDiscountInvalidReason(Discount discount) {
        LocalDateTime now = LocalDateTime.now();

        if (!discount.isActive()) {
            return "inactive";
        }

        if (now.isBefore(discount.startDate())) {
            return "not yet started";
        }

        if (now.isAfter(discount.endDate())) {
            return "expired";
        }

        if (discount.usageLimit() != null && discount.timesUsed() >= discount.usageLimit()) {
            return "usage limit reached";
        }

        return "invalid";
    }


    public domain.cart.Cart applyItemDiscountsToCart(domain.cart.Cart cart,
                                                     infrastructure.jdbc.JdbcItemRepository itemRepository) {
        List<Discount> activeDiscounts = getActiveDiscounts();

        // Create new cart with discounts applied
        domain.cart.Cart discountedCart = new domain.cart.Cart();

        for (var item : cart.items()) {
            // Get item details to check category
            var itemOpt = itemRepository.findByCode(item.itemCode());
            String itemCategory = itemOpt.isPresent() ? itemOpt.get().category() : null;

            // Find best discount for this item
            Discount bestDiscount = null;
            BigDecimal bestDiscountAmount = BigDecimal.ZERO;

            for (Discount discount : activeDiscounts) {
                if (discount.appliesTo(item.itemCode(), itemCategory)) {
                    BigDecimal discountAmount = discount.calculateItemDiscount(
                        new BigDecimal(item.unitPrice()), item.qty());

                    if (discountAmount.compareTo(bestDiscountAmount) > 0) {
                        bestDiscount = discount;
                        bestDiscountAmount = discountAmount;
                    }
                }
            }

            // Add item with discount applied
            if (bestDiscount != null && bestDiscountAmount.compareTo(BigDecimal.ZERO) > 0) {
                discountedCart.addWithDiscount(item.itemCode(), item.name(), item.unitPrice(),
                                             item.qty(), bestDiscountAmount, bestDiscount.discountCode());
            } else {
                // No discount applicable
                discountedCart.add(item.itemCode(), item.name(), item.unitPrice(), item.qty());
            }
        }

        return discountedCart;
    }

    public List<ItemDiscountInfo> getAvailableItemDiscounts(domain.cart.Cart cart,
                                                           infrastructure.jdbc.JdbcItemRepository itemRepository) {
        List<Discount> activeDiscounts = getActiveDiscounts();
        List<ItemDiscountInfo> availableDiscounts = new ArrayList<>();

        for (var item : cart.items()) {
            var itemOpt = itemRepository.findByCode(item.itemCode());
            String itemCategory = itemOpt.isPresent() ? itemOpt.get().category() : null;

            for (Discount discount : activeDiscounts) {
                if (discount.appliesTo(item.itemCode(), itemCategory)) {
                    BigDecimal discountAmount = discount.calculateItemDiscount(
                        new BigDecimal(item.unitPrice()), item.qty());

                    if (discountAmount.compareTo(BigDecimal.ZERO) > 0) {
                        availableDiscounts.add(new ItemDiscountInfo(
                            item.itemCode(), item.name(), discount.discountCode(),
                            discount.discountId(), discountAmount, discount.description()));
                    }
                }
            }
        }

        return availableDiscounts;
    }

    public Optional<Discount> validateDiscountCode(String discountCode) {
        var discountOpt = discountRepository.findByCode(discountCode);
        if (discountOpt.isPresent() && discountOpt.get().isValid()) {
            return discountOpt;
        }
        return Optional.empty();
    }

    public record ItemDiscountInfo(
        String itemCode,
        String itemName,
        String discountCode,
        int discountId,
        BigDecimal discountAmount,
        String description
    ) {}
}
