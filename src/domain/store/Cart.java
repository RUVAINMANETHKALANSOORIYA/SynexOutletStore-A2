package domain.store;

import java.math.BigDecimal;
import java.util.*;

public final class Cart {
    private final Map<String, Integer> qtyByCode = new LinkedHashMap<>();

    public void add(String itemCode, int qty) {
        if (qty <= 0) return;
        qtyByCode.merge(itemCode, qty, Integer::sum);
        if (qtyByCode.get(itemCode) <= 0) qtyByCode.remove(itemCode);
    }

    public void setQty(String itemCode, int qty) {
        if (qty <= 0) qtyByCode.remove(itemCode);
        else qtyByCode.put(itemCode, qty);
    }

    public void remove(String itemCode) {
        qtyByCode.remove(itemCode);
    }

    public void clear() {
        qtyByCode.clear();
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

    public BigDecimal total(java.util.function.Function<String, BigDecimal> priceLookup) {
        BigDecimal sum = BigDecimal.ZERO;
        for (var e : qtyByCode.entrySet()) {
            BigDecimal price = priceLookup.apply(e.getKey());
            if (price != null) {
                sum = sum.add(price.multiply(BigDecimal.valueOf(e.getValue())));
            }
        }
        return sum;
    }
}
