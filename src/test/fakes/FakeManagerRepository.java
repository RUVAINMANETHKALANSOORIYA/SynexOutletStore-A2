package fakes;

import domain.manager.*;
import domain.store.Item;
import ports.out.ManagerRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class FakeManagerRepository implements ManagerRepository {
    private final Map<String, Item> items = new HashMap<>();
    private final List<String> activities = new ArrayList<>();
    private final List<StockTransfer> transfers = new ArrayList<>();

    @Override
    public void createItem(String itemCode, String name, double price) {
        items.put(itemCode, new Item(itemCode, name, BigDecimal.valueOf(price), true, null));
    }

    @Override
    public void updateItem(String itemCode, String name, double price, boolean isActive) {
        items.put(itemCode, new Item(itemCode, name, BigDecimal.valueOf(price), isActive, null));
    }

    @Override
    public void deactivateItem(String itemCode) {
        Item item = items.get(itemCode);
        if (item != null) {
            items.put(itemCode, new Item(itemCode, item.name(), item.price(), false, item.category()));
        }
    }

    @Override
    public List<Item> getAllItems() { return new ArrayList<>(items.values()); }

    @Override
    public List<Item> getActiveItems() {
        return items.values().stream().filter(Item::isActive).toList();
    }

    @Override
    public Optional<Item> getItemByCode(String itemCode) {
        return Optional.ofNullable(items.get(itemCode));
    }

    @Override
    public List<String> getLowStockItems(int threshold) { return Collections.emptyList(); }

    @Override
    public void transferStock(String itemCode, int quantity, long managerId, String notes) {
        transfers.add(new StockTransfer(1L, itemCode, 1L, StockTransfer.TransferType.SHELF_TO_WEB, quantity, managerId, java.time.LocalDateTime.now(), notes));
    }

    @Override
    public List<StockTransfer> getTransferHistory(LocalDate date) { return transfers; }

    @Override
    public List<SalesReport> getDailySalesReport(LocalDate date) { return Collections.emptyList(); }

    @Override
    public List<StockReport> getStockReport() { return Collections.emptyList(); }

    @Override
    public List<String> getReorderReport(int threshold) { return Collections.emptyList(); }

    @Override
    public List<OrderReport> getBillReport(LocalDate startDate, LocalDate endDate) { return Collections.emptyList(); }

    @Override
    public void logActivity(long managerId, String activityType, String description, String itemCode) {
        activities.add(activityType + ": " + description);
    }
    
    public List<String> getActivities() { return activities; }
}
