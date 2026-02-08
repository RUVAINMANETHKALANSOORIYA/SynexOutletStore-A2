package infrastructure.jdbc;

import domain.manager.*;
import domain.store.Item;
import infrastructure.jdbc.Db;
import ports.out.ManagerRepository;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcManagerRepository implements ManagerRepository {

    @Override
    public void createItem(String itemCode, String name, double price) {
        String sql = "INSERT INTO items (item_code, name, price, is_active) VALUES (?, ?, ?, 1)";

        try (Connection conn = Db.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, itemCode);
            stmt.setString(2, name);
            stmt.setDouble(3, price);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to create item: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateItem(String itemCode, String name, double price, boolean isActive) {
        String sql = "UPDATE items SET name = ?, price = ?, is_active = ? WHERE item_code = ?";

        try (Connection conn = Db.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.setBoolean(3, isActive);
            stmt.setString(4, itemCode);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Item not found: " + itemCode);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update item: " + e.getMessage(), e);
        }
    }

    @Override
    public void deactivateItem(String itemCode) {
        String sql = "UPDATE items SET is_active = 0 WHERE item_code = ?";
        try (Connection conn = Db.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, itemCode);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Item not found: " + itemCode);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to deactivate item: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Item> getAllItems() {
        String sql = "SELECT item_code, name, price, is_active, category FROM items ORDER BY name";
        List<Item> items = new ArrayList<>();

        try (Connection conn = Db.get();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                items.add(new Item(
                    rs.getString("item_code"),
                    rs.getString("name"),
                    BigDecimal.valueOf(rs.getDouble("price")),
                    rs.getBoolean("is_active"),
                    rs.getString("category")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch all items: " + e.getMessage(), e);
        }

        return items;
    }

    @Override
    public List<Item> getActiveItems() {
        String sql = "SELECT item_code, name, price, is_active, category FROM items WHERE is_active = 1 ORDER BY name";
        List<Item> items = new ArrayList<>();

        try (Connection conn = Db.get();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                items.add(new Item(
                    rs.getString("item_code"),
                    rs.getString("name"),
                    BigDecimal.valueOf(rs.getDouble("price")),
                    rs.getBoolean("is_active"),
                    rs.getString("category")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch active items: " + e.getMessage(), e);
        }

        return items;
    }

    @Override
    public Optional<Item> getItemByCode(String itemCode) {
        String sql = "SELECT item_code, name, price, is_active, category FROM items WHERE item_code = ?";

        try (Connection conn = Db.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, itemCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Item(
                        rs.getString("item_code"),
                        rs.getString("name"),
                        BigDecimal.valueOf(rs.getDouble("price")),
                        rs.getBoolean("is_active"),
                        rs.getString("category")
                    ));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch item: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    @Override
    public List<String> getLowStockItems(int threshold) {
        String sql = """
            SELECT i.item_code, i.name,
                   COALESCE(SUM(s.shelf_qty + s.web_qty), 0) as total_stock
            FROM items i
            LEFT JOIN stock s ON i.item_code = s.item_code
            WHERE i.is_active = 1
            GROUP BY i.item_code, i.name
            HAVING total_stock <= ?
            ORDER BY total_stock ASC, i.name
            """;

        List<String> lowStockItems = new ArrayList<>();

        try (Connection conn = Db.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, threshold);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String itemCode = rs.getString("item_code");
                    String name = rs.getString("name");
                    int totalStock = rs.getInt("total_stock");
                    lowStockItems.add(String.format("%s - %s (Stock: %d)", itemCode, name, totalStock));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch low stock items: " + e.getMessage(), e);
        }

        return lowStockItems;
    }

    @Override
    public void transferStock(String itemCode, int quantity, long managerId, String notes) {
        // Enhanced input validation
        if (itemCode == null || itemCode.trim().isEmpty()) {
            throw domain.exception.StockTransferException.invalidItemCode(itemCode);
        }
        if (quantity <= 0) {
            throw domain.exception.StockTransferException.invalidQuantity(quantity);
        }
        if (quantity > 9999) { // Prevent extremely large transfers
            throw domain.exception.StockTransferException.invalidQuantity(quantity);
        }

        // Verify item exists and is active
        String checkItemSql = "SELECT is_active FROM items WHERE item_code = ?";
        try (Connection conn = Db.get();
             PreparedStatement checkStmt = conn.prepareStatement(checkItemSql)) {

            checkStmt.setString(1, itemCode.trim());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (!rs.next()) {
                    throw domain.exception.StockTransferException.itemNotFound(itemCode);
                }
                if (!rs.getBoolean("is_active")) {
                    throw domain.exception.StockTransferException.inactiveItem(itemCode);
                }
            }
        } catch (SQLException e) {
            throw domain.exception.StockTransferException.concurrentModification(itemCode);
        }

        String selectStockSql = """
            SELECT stock_id, shelf_qty, web_qty FROM stock 
            WHERE item_code = ? AND shelf_qty >= ? 
            ORDER BY expiry_date ASC, received_date ASC 
            LIMIT 1 FOR UPDATE
            """;

        String updateStockSql = "UPDATE stock SET shelf_qty = shelf_qty - ?, web_qty = web_qty + ? WHERE stock_id = ?";

        String insertTransferSql = """
            INSERT INTO stock_transfers (item_code, stock_id, transfer_type, quantity, transferred_by, notes)
            VALUES (?, ?, 'SHELF_TO_WEB', ?, ?, ?)
            """;

        try (Connection conn = Db.get()) {
            conn.setAutoCommit(false);

            try {
                // Find suitable stock batch with sufficient shelf quantity
                Long stockId = null;
                int availableShelfQty = 0;

                try (PreparedStatement selectStmt = conn.prepareStatement(selectStockSql)) {
                    selectStmt.setString(1, itemCode.trim());
                    selectStmt.setInt(2, quantity);

                    try (ResultSet rs = selectStmt.executeQuery()) {
                        if (rs.next()) {
                            stockId = rs.getLong("stock_id");
                            availableShelfQty = rs.getInt("shelf_qty");
                        } else {
                            // Get actual available quantity for better error message
                            String getAvailableQtySql = "SELECT COALESCE(SUM(shelf_qty), 0) as total_shelf_qty FROM stock WHERE item_code = ?";
                            try (PreparedStatement availableStmt = conn.prepareStatement(getAvailableQtySql)) {
                                availableStmt.setString(1, itemCode.trim());
                                try (ResultSet availableRs = availableStmt.executeQuery()) {
                                    if (availableRs.next()) {
                                        availableShelfQty = availableRs.getInt("total_shelf_qty");
                                    }
                                }
                            }
                            throw domain.exception.StockTransferException.insufficientStock(itemCode, quantity, availableShelfQty);
                        }
                    }
                }

                // Double-check we have valid stock ID
                if (stockId == null || stockId <= 0) {
                    throw domain.exception.StockTransferException.concurrentModification(itemCode);
                }

                // Update stock quantities
                int updatedRows;
                try (PreparedStatement updateStmt = conn.prepareStatement(updateStockSql)) {
                    updateStmt.setInt(1, quantity);
                    updateStmt.setInt(2, quantity);
                    updateStmt.setLong(3, stockId);
                    updatedRows = updateStmt.executeUpdate();
                }

                if (updatedRows == 0) {
                    throw domain.exception.StockTransferException.concurrentModification(itemCode);
                }

                // Record transfer
                try (PreparedStatement insertStmt = conn.prepareStatement(insertTransferSql)) {
                    insertStmt.setString(1, itemCode.trim());
                    insertStmt.setLong(2, stockId);
                    insertStmt.setInt(3, quantity);
                    insertStmt.setLong(4, managerId);
                    insertStmt.setString(5, notes != null ? notes.trim() : null);
                    insertStmt.executeUpdate();
                }

                conn.commit();

                // Log successful transfer
                java.util.logging.Logger.getLogger(this.getClass().getName())
                    .info(String.format("Stock transfer completed: %d units of %s from SHELF to WEB by manager %d",
                         quantity, itemCode, managerId));

            } catch (domain.exception.StockTransferException e) {
                conn.rollback();
                throw e; // Re-throw our custom exceptions
            } catch (SQLException e) {
                conn.rollback();
                if (e.getMessage().contains("foreign key")) {
                    throw domain.exception.StockTransferException.itemNotFound(itemCode);
                } else if (e.getMessage().contains("duplicate")) {
                    throw domain.exception.StockTransferException.concurrentModification(itemCode);
                } else {
                    throw new RuntimeException("Database error during stock transfer: " + e.getMessage(), e);
                }
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Stock transfer failed: " + e.getMessage(), e);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database connection error during stock transfer: " + e.getMessage(), e);
        }
    }

    @Override
    public List<StockTransfer> getTransferHistory(LocalDate date) {
        String sql = """
            SELECT st.transfer_id, st.item_code, st.stock_id, st.transfer_type,
                   st.quantity, st.transferred_by, st.transfer_date, st.notes
            FROM stock_transfers st
            WHERE DATE(st.transfer_date) = ?
            ORDER BY st.transfer_date DESC
            """;

        List<StockTransfer> transfers = new ArrayList<>();

        try (Connection conn = Db.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(date));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transfers.add(new StockTransfer(
                        rs.getLong("transfer_id"),
                        rs.getString("item_code"),
                        rs.getLong("stock_id"),
                        StockTransfer.TransferType.valueOf(rs.getString("transfer_type")),
                        rs.getInt("quantity"),
                        rs.getLong("transferred_by"),
                        rs.getTimestamp("transfer_date").toLocalDateTime(),
                        rs.getString("notes")
                    ));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch transfer history: " + e.getMessage(), e);
        }

        return transfers;
    }

    @Override
    public List<SalesReport> getDailySalesReport(LocalDate date) {
        String sql = """
            SELECT i.item_code, i.name, 
                   SUM(oi.quantity) as total_quantity,
                   oi.unit_price,
                   SUM(oi.line_total) as total_revenue
            FROM orders o
            JOIN order_items oi ON o.order_id = oi.order_id
            JOIN items i ON oi.item_code = i.item_code
            WHERE DATE(o.created_at) = ? AND o.status = 'PAID'
            GROUP BY i.item_code, i.name, oi.unit_price
            ORDER BY total_revenue DESC
            """;

        List<SalesReport> salesReports = new ArrayList<>();

        try (Connection conn = Db.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(date));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    salesReports.add(new SalesReport(
                        rs.getString("item_code"),
                        rs.getString("name"),
                        rs.getInt("total_quantity"),
                        rs.getBigDecimal("unit_price"),
                        rs.getBigDecimal("total_revenue"),
                        date
                    ));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to generate daily sales report: " + e.getMessage(), e);
        }

        return salesReports;
    }

    @Override
    public List<StockReport> getStockReport() {
        String sql = """
            SELECT s.item_code, i.name, s.batch_code, 
                   s.received_date as purchase_date,
                   s.expiry_date, s.shelf_qty, s.web_qty, i.price, s.supplier
            FROM stock s
            JOIN items i ON s.item_code = i.item_code
            WHERE i.is_active = 1 AND (s.shelf_qty > 0 OR s.web_qty > 0)
            ORDER BY s.item_code, s.expiry_date ASC
            """;

        List<StockReport> stockReports = new ArrayList<>();

        try (Connection conn = Db.get();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                stockReports.add(new StockReport(
                    rs.getString("item_code"),
                    rs.getString("name"),
                    rs.getString("batch_code"),
                    rs.getDate("purchase_date") != null ? rs.getDate("purchase_date").toLocalDate() : null,
                    rs.getDate("expiry_date").toLocalDate(),
                    rs.getInt("shelf_qty"),
                    rs.getInt("web_qty"),
                    rs.getBigDecimal("price"),
                    rs.getString("supplier")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to generate stock report: " + e.getMessage(), e);
        }

        return stockReports;
    }

    @Override
    public List<String> getReorderReport(int threshold) {
        return getLowStockItems(threshold); // Reuse the same logic
    }

    @Override
    public List<OrderReport> getBillReport(LocalDate startDate, LocalDate endDate) {
        String sql = """
            SELECT o.order_id, o.order_type, o.payment_method, o.total_amount, o.created_at,
                   COALESCE(c.full_name, 'POS Sale') as customer_info
            FROM orders o
            LEFT JOIN customers c ON o.customer_id = c.customer_id
            WHERE DATE(o.created_at) BETWEEN ? AND ? AND o.status = 'PAID'
            ORDER BY o.created_at DESC
            """;

        List<OrderReport> orderReports = new ArrayList<>();

        try (Connection conn = Db.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orderReports.add(new OrderReport(
                        rs.getLong("order_id"),
                        rs.getString("order_type"),
                        rs.getString("payment_method"),
                        rs.getBigDecimal("total_amount"),
                        rs.getString("customer_info"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                    ));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to generate bill report: " + e.getMessage(), e);
        }

        return orderReports;
    }

    @Override
    public void logActivity(long managerId, String activityType, String description, String itemCode) {
        String sql = """
            INSERT INTO manager_activities (manager_user_id, activity_type, description, target_item_code)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection conn = Db.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, managerId);
            stmt.setString(2, activityType);
            stmt.setString(3, description);
            stmt.setString(4, itemCode);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to log manager activity: " + e.getMessage(), e);
        }
    }
}
