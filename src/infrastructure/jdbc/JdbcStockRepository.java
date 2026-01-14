package infrastructure.jdbc;

import ports.out.StockRepository;

import java.sql.*;

public final class JdbcStockRepository implements StockRepository {

    /**
     * Check available web stock for an item
     */
    public int getAvailableWebStock(String itemCode) {
        String sql = "SELECT SUM(web_qty) as total FROM stock WHERE item_code = ?";

        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, itemCode);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check web stock availability", e);
        }
    }

    /**
     * Check available shelf stock for an item
     */
    public int getAvailableShelfStock(String itemCode) {
        String sql = "SELECT SUM(shelf_qty) as total FROM stock WHERE item_code = ?";

        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, itemCode);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check shelf stock availability", e);
        }
    }

    @Override
    public void deductWebStockFifo(Connection c, long orderId, String itemCode, int qtyNeeded) {
        // Lock rows for this item to avoid concurrency stock corruption
        String selectSql = """
            SELECT stock_id, web_qty
            FROM stock
            WHERE item_code = ?
              AND web_qty > 0
            ORDER BY
              (expiry_date IS NULL) ASC,
              expiry_date ASC,
              created_at ASC
            FOR UPDATE
        """;

        String updateSql = "UPDATE stock SET web_qty = web_qty - ? WHERE stock_id = ?";
        String movementSql = """
            INSERT INTO stock_movements (stock_id, order_id, quantity, source)
            VALUES (?, ?, ?, 'WEB')
        """;

        try (PreparedStatement sel = c.prepareStatement(selectSql)) {
            sel.setString(1, itemCode);

            try (ResultSet rs = sel.executeQuery()) {
                int remaining = qtyNeeded;

                while (rs.next() && remaining > 0) {
                    long stockId = rs.getLong("stock_id");
                    int available = rs.getInt("web_qty");

                    int take = Math.min(available, remaining);

                    try (PreparedStatement upd = c.prepareStatement(updateSql)) {
                        upd.setInt(1, take);
                        upd.setLong(2, stockId);
                        upd.executeUpdate();
                    }

                    try (PreparedStatement mov = c.prepareStatement(movementSql)) {
                        mov.setLong(1, stockId);
                        mov.setLong(2, orderId);
                        mov.setInt(3, take);
                        mov.executeUpdate();
                    }

                    remaining -= take;
                }

                if (remaining > 0) {
                    throw new IllegalStateException("Not enough WEB stock for item " + itemCode +
                            ". Missing qty: " + remaining);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("deductWebStockFifo failed", e);
        }
    }

    @Override
    public void deductShelfStockFifo(Connection c, long orderId, String itemCode, int qtyNeeded) {

        String selectSql = """
        SELECT stock_id, shelf_qty
        FROM stock
        WHERE item_code = ?
          AND shelf_qty > 0
        ORDER BY
          (expiry_date IS NULL) ASC,
          expiry_date ASC,
          created_at ASC
        FOR UPDATE
    """;

        String updateSql = "UPDATE stock SET shelf_qty = shelf_qty - ? WHERE stock_id = ?";
        String movementSql = """
        INSERT INTO stock_movements (stock_id, order_id, quantity, source)
        VALUES (?, ?, ?, 'SHELF')
    """;

        try (PreparedStatement sel = c.prepareStatement(selectSql)) {
            sel.setString(1, itemCode);

            try (ResultSet rs = sel.executeQuery()) {
                int remaining = qtyNeeded;

                while (rs.next() && remaining > 0) {
                    long stockId = rs.getLong("stock_id");
                    int available = rs.getInt("shelf_qty");

                    int take = Math.min(available, remaining);

                    try (PreparedStatement upd = c.prepareStatement(updateSql)) {
                        upd.setInt(1, take);
                        upd.setLong(2, stockId);
                        upd.executeUpdate();
                    }

                    try (PreparedStatement mov = c.prepareStatement(movementSql)) {
                        mov.setLong(1, stockId);
                        mov.setLong(2, orderId);
                        mov.setInt(3, take);
                        mov.executeUpdate();
                    }

                    remaining -= take;
                }

                if (remaining > 0) {
                    throw new IllegalStateException("Not enough SHELF stock for item " + itemCode +
                            ". Missing qty: " + remaining);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("deductShelfStockFifo failed", e);
        }
    }

}
