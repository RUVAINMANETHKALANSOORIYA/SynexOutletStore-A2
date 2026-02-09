package domain.store;

import java.math.BigDecimal;

public record Item(
        String code,
        String name,
        BigDecimal price,
        boolean isActive,
        String category  //
) {
    // Convenience constructor for active items (backwards compatibility)
    public Item(String code, String name, BigDecimal price) {
        this(code, name, price, true, null);
    }

    // Constructor with category
    public Item(String code, String name, BigDecimal price, String category) {
        this(code, name, price, true, category);
    }

    // Convenience method for backwards compatibility
    public String itemCode() {
        return code;
    }
}
