package fakes;

import ports.out.StockRepository;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.*;

public class FakeStockRepository implements StockRepository {
    private static class StockBatch {
        String itemCode;
        LocalDate expiryDate;
        int webQty;
        int shelfQty;

        StockBatch(String itemCode, LocalDate expiryDate, int webQty, int shelfQty) {
            this.itemCode = itemCode;
            this.expiryDate = expiryDate;
            this.webQty = webQty;
            this.shelfQty = shelfQty;
        }
    }

    private final List<StockBatch> batches = new ArrayList<>();

    public void addBatch(String itemCode, LocalDate expiry, int webQty, int shelfQty) {
        batches.add(new StockBatch(itemCode, expiry, webQty, shelfQty));
        // Sort by expiry date (FEFO)
        batches.sort(Comparator.comparing(b -> b.expiryDate, Comparator.nullsLast(Comparator.naturalOrder())));
    }

    public void setWebStock(String itemCode, int qty) {
        addBatch(itemCode, LocalDate.now().plusDays(30), qty, 0);
    }

    public void setShelfStock(String itemCode, int qty) {
        addBatch(itemCode, LocalDate.now().plusDays(30), 0, qty);
    }

    @Override
    public void deductWebStockFifo(Connection c, long orderId, String itemCode, int qtyNeeded) {
        int remaining = qtyNeeded;
        for (StockBatch b : batches) {
            if (b.itemCode.equals(itemCode) && b.webQty > 0) {
                int take = Math.min(b.webQty, remaining);
                b.webQty -= take;
                remaining -= take;
                if (remaining == 0) break;
            }
        }
        if (remaining > 0) {
            throw new IllegalStateException("Not enough WEB stock for item " + itemCode);
        }
    }

    @Override
    public void deductShelfStockFifo(Connection c, long orderId, String itemCode, int qtyNeeded) {
        int remaining = qtyNeeded;
        for (StockBatch b : batches) {
            if (b.itemCode.equals(itemCode) && b.shelfQty > 0) {
                int take = Math.min(b.shelfQty, remaining);
                b.shelfQty -= take;
                remaining -= take;
                if (remaining == 0) break;
            }
        }
        if (remaining > 0) {
            throw new IllegalStateException("Not enough SHELF stock for item " + itemCode);
        }
    }

    public int getAvailableWebStock(String itemCode) {
        return batches.stream().filter(b -> b.itemCode.equals(itemCode)).mapToInt(b -> b.webQty).sum();
    }

    public int getAvailableShelfStock(String itemCode) {
        return batches.stream().filter(b -> b.itemCode.equals(itemCode)).mapToInt(b -> b.shelfQty).sum();
    }
}
