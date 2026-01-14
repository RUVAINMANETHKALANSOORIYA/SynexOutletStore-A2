package domain.cart;

import java.util.*;

public class Cart {
    private final Map<String, CartItem> items = new LinkedHashMap<>();

    public void add(String code, String name, double unitPrice, int qty) {
        CartItem existing = items.get(code);
        if (existing == null) {
            items.put(code, new CartItem(code, name, unitPrice, qty));
        } else {
            items.put(code, new CartItem(code, name, unitPrice, existing.qty() + qty));
        }
    }

    public Collection<CartItem> items() { return items.values(); }
    public boolean isEmpty() { return items.isEmpty(); }

    public double total() {
        double t = 0;
        for (CartItem i : items.values()) t += i.lineTotal();
        return t;
    }

    public void clear() { items.clear(); }
}
