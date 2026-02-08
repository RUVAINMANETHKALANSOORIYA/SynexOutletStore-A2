package domain.store;

import domain.exception.InvalidCartOperationException;
import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Logger;

public final class Cart {
    private static final Logger logger = Logger.getLogger(Cart.class.getName());
    private static final int MAX_CART_SIZE = 50; // Maximum unique items
    private static final int MAX_ITEM_QUANTITY = 999; // Maximum quantity per item

    private final Map<String, Integer> qtyByCode = new LinkedHashMap<>();

    public void add(String itemCode, int qty) {
        validateItemCode(itemCode);
        validateQuantity(qty);

        if (qty <= 0) {
            logger.warning("Attempted to add non-positive quantity (" + qty + ") for item: " + itemCode);
            return;
        }

        // Normalize item code to prevent duplicates with different casing/spacing
        String normalizedCode = itemCode.trim().toUpperCase();

        // Check cart size limit for new items
        if (!qtyByCode.containsKey(normalizedCode) && qtyByCode.size() >= MAX_CART_SIZE) {
            throw InvalidCartOperationException.cartSizeLimit(MAX_CART_SIZE);
        }

        // Check if adding quantity would exceed maximum
        int currentQty = qtyByCode.getOrDefault(normalizedCode, 0);

        // Prevent integer overflow
        if (currentQty > Integer.MAX_VALUE - qty) {
            throw new InvalidCartOperationException(
                String.format("Adding %d items would cause overflow for item %s. Current: %d",
                qty, normalizedCode, currentQty));
        }

        if (currentQty + qty > MAX_ITEM_QUANTITY) {
            throw new InvalidCartOperationException(
                String.format("Adding %d items would exceed maximum quantity (%d) for item %s. Current: %d",
                qty, MAX_ITEM_QUANTITY, normalizedCode, currentQty));
        }

        qtyByCode.merge(normalizedCode, qty, Integer::sum);
        if (qtyByCode.get(normalizedCode) <= 0) {
            qtyByCode.remove(normalizedCode);
        }

        logger.info("Added " + qty + " units of item " + normalizedCode + " to cart. New quantity: " + qtyByCode.get(normalizedCode));
    }

    public void setQty(String itemCode, int qty) {
        validateItemCode(itemCode);

        if (qty < 0) {
            throw InvalidCartOperationException.invalidQuantity(qty);
        }

        if (qty > MAX_ITEM_QUANTITY) {
            throw new InvalidCartOperationException(
                String.format("Quantity %d exceeds maximum allowed (%d) for item %s",
                qty, MAX_ITEM_QUANTITY, itemCode));
        }

        // Normalize item code for consistency
        String normalizedCode = itemCode.trim().toUpperCase();

        if (qty <= 0) {
            qtyByCode.remove(normalizedCode);
            logger.info("Removed item " + normalizedCode + " from cart (quantity set to " + qty + ")");
        } else {
            qtyByCode.put(normalizedCode, qty);
            logger.info("Set quantity for item " + normalizedCode + " to " + qty);
        }
    }

    public void remove(String itemCode) {
        validateItemCode(itemCode);

        if (!qtyByCode.containsKey(itemCode)) {
            throw InvalidCartOperationException.itemNotFound(itemCode);
        }

        qtyByCode.remove(itemCode);
        logger.info("Removed item " + itemCode + " from cart");
    }

    public void clear() {
        int itemCount = qtyByCode.size();
        qtyByCode.clear();
        logger.info("Cleared cart with " + itemCount + " items");
    }

    public boolean isEmpty() {
        return qtyByCode.isEmpty();
    }

    public Map<String, Integer> items() {
        return Collections.unmodifiableMap(qtyByCode);
    }

    public int totalItems() {
        return qtyByCode.values().stream().mapToInt(Integer::intValue).sum();
    }

    public int uniqueItemCount() {
        return qtyByCode.size();
    }

    public boolean containsItem(String itemCode) {
        validateItemCode(itemCode);
        return qtyByCode.containsKey(itemCode);
    }

    public int getQuantity(String itemCode) {
        validateItemCode(itemCode);
        return qtyByCode.getOrDefault(itemCode, 0);
    }

    public BigDecimal total(java.util.function.Function<String, BigDecimal> priceLookup) {
        if (priceLookup == null) {
            throw new IllegalArgumentException("Price lookup function cannot be null");
        }

        if (isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal sum = BigDecimal.ZERO;
        for (var e : qtyByCode.entrySet()) {
            try {
                BigDecimal price = priceLookup.apply(e.getKey());
                if (price == null) {
                    logger.warning("No price found for item: " + e.getKey());
                    continue;
                }
                if (price.compareTo(BigDecimal.ZERO) < 0) {
                    logger.warning("Negative price detected for item " + e.getKey() + ": " + price);
                    continue;
                }
                sum = sum.add(price.multiply(BigDecimal.valueOf(e.getValue())));
            } catch (Exception ex) {
                logger.severe("Error calculating price for item " + e.getKey() + ": " + ex.getMessage());
                // Continue with other items rather than failing entirely
            }
        }
        return sum;
    }

    /**
     * Validates that checkout can proceed
     * @throws InvalidCartOperationException if cart is invalid for checkout
     */
    public void validateForCheckout() {
        if (isEmpty()) {
            throw InvalidCartOperationException.emptyCart();
        }

        // Validate all items in cart
        int totalItems = 0;
        for (var entry : qtyByCode.entrySet()) {
            String itemCode = entry.getKey();
            Integer quantity = entry.getValue();

            // Check for null or invalid quantities
            if (quantity == null || quantity <= 0) {
                throw new InvalidCartOperationException("Invalid quantity for item " + itemCode + ": " + quantity);
            }

            // Check for extremely large quantities that might indicate data corruption
            if (quantity > MAX_ITEM_QUANTITY) {
                throw new InvalidCartOperationException(
                    String.format("Quantity %d exceeds maximum allowed (%d) for item %s",
                    quantity, MAX_ITEM_QUANTITY, itemCode));
            }

            totalItems += quantity;

            // Prevent checkout with suspiciously large total quantities
            if (totalItems > 50000) { // Reasonable business limit
                throw new InvalidCartOperationException(
                    "Total cart quantity (" + totalItems + ") exceeds reasonable checkout limit");
            }
        }

        // Additional business rule validations
        if (qtyByCode.size() > MAX_CART_SIZE) {
            throw new InvalidCartOperationException(
                String.format("Cart contains too many unique items (%d). Maximum allowed: %d",
                qtyByCode.size(), MAX_CART_SIZE));
        }

        // Validate item codes format (basic validation)
        for (String itemCode : qtyByCode.keySet()) {
            if (itemCode.length() > 20) { // Reasonable item code length limit
                throw new InvalidCartOperationException("Item code too long: " + itemCode);
            }
            if (!itemCode.matches("^[A-Z0-9_-]+$")) { // Only allow alphanumeric, underscore, hyphen
                logger.warning("Cart contains item with non-standard code format: " + itemCode);
            }
        }

        logger.info(String.format("Cart validation passed: %d unique items, %d total quantity",
                   qtyByCode.size(), totalItems));
    }

    private void validateItemCode(String itemCode) {
        if (itemCode == null || itemCode.trim().isEmpty()) {
            throw InvalidCartOperationException.invalidItemCode(itemCode);
        }
    }

    private void validateQuantity(int qty) {
        if (qty < 0) {
            throw InvalidCartOperationException.invalidQuantity(qty);
        }
    }

    @Override
    public String toString() {
        return String.format("Cart{items=%d, totalQty=%d}", uniqueItemCount(), totalItems());
    }
}
