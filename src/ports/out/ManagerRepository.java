package ports.out;

import domain.manager.*;
import domain.store.Item;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ManagerRepository {

    // Item Management
    void createItem(String itemCode, String name, double price);
    void updateItem(String itemCode, String name, double price, boolean isActive);
    void deactivateItem(String itemCode);
    List<Item> getAllItems();
    List<Item> getActiveItems();
    Optional<Item> getItemByCode(String itemCode);

    // Stock Management & Transfers
    List<String> getLowStockItems(int threshold);
    void transferStock(String itemCode, int quantity, long managerId, String notes);
    List<StockTransfer> getTransferHistory(LocalDate date);

    // Reports
    List<SalesReport> getDailySalesReport(LocalDate date);
    List<StockReport> getStockReport();
    List<String> getReorderReport(int threshold);
    List<OrderReport> getBillReport(LocalDate startDate, LocalDate endDate);

    // Activity Logging
    void logActivity(long managerId, String activityType, String description, String itemCode);
}
