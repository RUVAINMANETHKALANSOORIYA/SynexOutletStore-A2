package domain.cart;

import java.math.BigDecimal;
import java.util.*;

public class Cart {
    private final Map<String, CartItem> items = new LinkedHashMap<>();

    public void add(String code, String name, double unitPrice, int qty) {
        CartItem existing = items.get(code);
        if (existing == null) {
            items.put(code, new CartItem(code, name, unitPrice, qty));
        } else {
            // When adding more of same item, preserve any existing discount
            items.put(code, new CartItem(code, name, unitPrice, existing.qty() + qty,
                                       existing.discountAmount(), existing.appliedDiscountCode()));
        }
    }

    public void addWithDiscount(String code, String name, double unitPrice, int qty,
                               BigDecimal discountAmount, String discountCode) {
        items.put(code, new CartItem(code, name, unitPrice, qty, discountAmount, discountCode));
    }

    public void applyDiscountToItem(String itemCode, BigDecimal discountAmount, String discountCode) {
        CartItem existing = items.get(itemCode);
        if (existing != null) {
            items.put(itemCode, new CartItem(existing.itemCode(), existing.name(),
                                           existing.unitPrice(), existing.qty(),
                                           discountAmount, discountCode));
        }
    }

    public Collection<CartItem> items() { return items.values(); }
    public boolean isEmpty() { return items.isEmpty(); }

    public double total() {
        double t = 0;
        for (CartItem i : items.values()) t += i.lineTotal();
        return t;
    }

    public BigDecimal discountedTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items.values()) {
            total = total.add(item.discountedLineTotal());
        }
        return total;
    }

    public BigDecimal totalDiscountAmount() {
        BigDecimal totalDiscount = BigDecimal.ZERO;
        for (CartItem item : items.values()) {
            if (item.discountAmount() != null) {
                totalDiscount = totalDiscount.add(item.discountAmount());
            }
        }
        return totalDiscount;
    }

    public void clear() { items.clear(); }
}
