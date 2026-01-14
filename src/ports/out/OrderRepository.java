package ports.out;

import domain.orders.Order;
import java.sql.Connection;

public interface OrderRepository {
    long insertOrder(Connection c, Order order);
    void insertOrderItem(Connection c, long orderId, String itemCode, int qty, double unitPrice);
}
