package infrastructure.jdbc;

import domain.orders.Order;
import ports.out.OrderRepository;

import java.sql.*;

public final class JdbcOrderRepository implements OrderRepository {

    @Override
    public long insertOrder(Connection c, Order order) {
        String sql = """
            INSERT INTO orders (cashier_user_id, customer_id, order_type, payment_method, total_amount)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // cashier_user_id (for PHYSICAL orders)
            if (order.userId() == null) ps.setNull(1, Types.BIGINT);
            else ps.setLong(1, order.userId());

            // customer_id (for ONLINE orders)
            if (order.customerId() == null) ps.setNull(2, Types.BIGINT);
            else ps.setLong(2, order.customerId());

            ps.setString(3, order.orderType().name());
            ps.setString(4, order.paymentMethod().name());
            ps.setDouble(5, order.totalAmount());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
            throw new SQLException("No generated key for order");
        } catch (SQLException e) {
            throw new RuntimeException("insertOrder failed", e);
        }
    }

    @Override
    public void insertOrderItem(Connection c, long orderId, String itemCode, int qty, double unitPrice) {
        String sql = """
            INSERT INTO order_items (order_id, item_code, quantity, unit_price, line_total)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            ps.setString(2, itemCode);
            ps.setInt(3, qty);
            ps.setDouble(4, unitPrice);
            ps.setDouble(5, qty * unitPrice);  // Calculate line_total
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("insertOrderItem failed", e);
        }
    }
}
