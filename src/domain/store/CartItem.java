package domain.store;

import java.math.BigDecimal;

public record CartItem(
        String itemCode,
        String name,
        BigDecimal unitPrice,
        int quantity
) {
    public BigDecimal lineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
