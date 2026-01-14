package domain.store;

import java.math.BigDecimal;

public record Item(
        String code,
        String name,
        BigDecimal price,
        boolean isActive
) {
    // Convenience constructor for active items (backwards compatibility)
    public Item(String code, String name, BigDecimal price) {
        this(code, name, price, true);
    }

    // Convenience method for backwards compatibility
    public String itemCode() {
        return code;
    }
}
