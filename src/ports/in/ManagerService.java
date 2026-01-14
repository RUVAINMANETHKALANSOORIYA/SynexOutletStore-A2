package ports.in;

import domain.manager.*;
import domain.store.Item;
import java.time.LocalDate;
import java.util.List;

public interface ManagerService {

    // Item Management
    void createItem(String itemCode, String name, double price, long managerId);
    void updateItem(String itemCode, String name, double price, boolean isActive, long managerId);
    void deactivateItem(String itemCode, long managerId);
    List<Item> getAllItems();
    List<Item> getActiveItems();

    // Stock Management
    List<String> getLowStockAlerts();
    void transferStockShelfToWeb(String itemCode, int quantity, long managerId, String notes);
    List<StockTransfer> getTodayTransfers();

    // Reports
    List<SalesReport> generateDailySalesReport(LocalDate date);
    List<StockReport> generateStockReport();
    List<String> generateReorderReport();
    List<OrderReport> generateBillReport(LocalDate startDate, LocalDate endDate);
}
