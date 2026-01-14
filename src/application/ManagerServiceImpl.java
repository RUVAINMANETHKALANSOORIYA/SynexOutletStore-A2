package application;

import domain.manager.*;
import domain.store.Item;
import ports.in.ManagerService;
import ports.out.ManagerRepository;

import java.time.LocalDate;
import java.util.List;

public class ManagerServiceImpl implements ManagerService {

    private static final int LOW_STOCK_THRESHOLD = 50;

    private final ManagerRepository managerRepository;

    public ManagerServiceImpl(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    @Override
    public void createItem(String itemCode, String name, double price, long managerId) {
        if (itemCode == null || itemCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Item code cannot be empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be empty");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Item price must be positive");
        }

        // Check if item already exists
        if (managerRepository.getItemByCode(itemCode).isPresent()) {
            throw new IllegalArgumentException("Item with code " + itemCode + " already exists");
        }

        managerRepository.createItem(itemCode, name, price);
        managerRepository.logActivity(managerId, "ITEM_CREATED",
            "Created new item: " + name + " with price " + price, itemCode);
    }

    @Override
    public void updateItem(String itemCode, String name, double price, boolean isActive, long managerId) {
        if (itemCode == null || itemCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Item code cannot be empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be empty");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Item price must be positive");
        }

        // Check if item exists
        if (managerRepository.getItemByCode(itemCode).isEmpty()) {
            throw new IllegalArgumentException("Item with code " + itemCode + " not found");
        }

        managerRepository.updateItem(itemCode, name, price, isActive);
        managerRepository.logActivity(managerId, "ITEM_UPDATED",
            "Updated item: " + name + " with price " + price + ", active: " + isActive, itemCode);
    }

    @Override
    public void deactivateItem(String itemCode, long managerId) {
        if (itemCode == null || itemCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Item code cannot be empty");
        }

        var item = managerRepository.getItemByCode(itemCode);
        if (item.isEmpty()) {
            throw new IllegalArgumentException("Item with code " + itemCode + " not found");
        }

        managerRepository.deactivateItem(itemCode);
        managerRepository.logActivity(managerId, "ITEM_DEACTIVATED",
            "Deactivated item: " + item.get().name(), itemCode);
    }

    @Override
    public List<Item> getAllItems() {
        return managerRepository.getAllItems();
    }

    @Override
    public List<Item> getActiveItems() {
        return managerRepository.getActiveItems();
    }

    @Override
    public List<String> getLowStockAlerts() {
        return managerRepository.getLowStockItems(LOW_STOCK_THRESHOLD);
    }

    @Override
    public void transferStockShelfToWeb(String itemCode, int quantity, long managerId, String notes) {
        if (itemCode == null || itemCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Item code cannot be empty");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Transfer quantity must be positive");
        }

        // Check if item exists and is active
        var item = managerRepository.getItemByCode(itemCode);
        if (item.isEmpty()) {
            throw new IllegalArgumentException("Item with code " + itemCode + " not found");
        }
        if (!item.get().isActive()) {
            throw new IllegalArgumentException("Cannot transfer stock for inactive item: " + itemCode);
        }

        managerRepository.transferStock(itemCode, quantity, managerId, notes);
        managerRepository.logActivity(managerId, "STOCK_TRANSFER",
            "Transferred " + quantity + " units from SHELF to WEB for item " + itemCode, itemCode);
    }

    @Override
    public List<StockTransfer> getTodayTransfers() {
        return managerRepository.getTransferHistory(LocalDate.now());
    }

    @Override
    public List<SalesReport> generateDailySalesReport(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot generate report for future date");
        }

        return managerRepository.getDailySalesReport(date);
    }

    @Override
    public List<StockReport> generateStockReport() {
        return managerRepository.getStockReport();
    }

    @Override
    public List<String> generateReorderReport() {
        return managerRepository.getReorderReport(LOW_STOCK_THRESHOLD);
    }

    @Override
    public List<OrderReport> generateBillReport(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(7); // Default to last 7 days
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        if (endDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("End date cannot be in the future");
        }

        return managerRepository.getBillReport(startDate, endDate);
    }
}
