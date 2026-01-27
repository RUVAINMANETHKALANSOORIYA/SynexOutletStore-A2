package fakes;

import domain.orders.Order;
import ports.out.OrderRepository;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class FakeOrderRepository implements OrderRepository {
    private final AtomicLong orderIdGenerator = new AtomicLong(1);
    private final List<Order> orders = new ArrayList<>();
    private final List<OrderItemRecord> orderItems = new ArrayList<>();

    public record OrderItemRecord(long orderId, String itemCode, int qty, double unitPrice) {}

    @Override
    public long insertOrder(Connection c, Order order) {
        long id = orderIdGenerator.getAndIncrement();
        orders.add(order);
        return id;
    }

    @Override
    public void insertOrderItem(Connection c, long orderId, String itemCode, int qty, double unitPrice) {
        orderItems.add(new OrderItemRecord(orderId, itemCode, qty, unitPrice));
    }

    public List<Order> getOrders() { return orders; }
    public List<OrderItemRecord> getOrderItems() { return orderItems; }
}
