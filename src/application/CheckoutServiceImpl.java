package application;

import domain.cart.Cart;
import domain.cart.CartItem;
import domain.orders.Order;
import domain.orders.OrderType;
import domain.orders.PaymentMethod;
import infrastructure.jdbc.Db;
import infrastructure.jdbc.JdbcStockRepository;
import ports.in.CheckoutService;
import ports.out.OrderRepository;
import ports.out.StockRepository;

import java.sql.Connection;

public final class CheckoutServiceImpl implements CheckoutService {

    private final OrderRepository orders;
    private final StockRepository stock;

    public CheckoutServiceImpl(OrderRepository orders, StockRepository stock) {
        this.orders = orders;
        this.stock = stock;
    }

    @Override
    public long placeOnlineOrder(long customerId, Cart cart, PaymentMethod paymentMethod) {
        // ✅ 1. Validate cart is not empty
        if (cart == null || cart.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        // ✅ 2. Validate payment method
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method is required");
        }

        // ✅ 3. Check stock availability BEFORE starting transaction
        if (stock instanceof JdbcStockRepository) {
            JdbcStockRepository jdbcStock = (JdbcStockRepository) stock;

            for (CartItem item : cart.items()) {
                int available = jdbcStock.getAvailableWebStock(item.itemCode());
                if (available < item.qty()) {
                    throw new IllegalStateException(
                        "Insufficient web stock for item " + item.itemCode() +
                        ". Requested: " + item.qty() + ", Available: " + available
                    );
                }
            }
        }

        try (Connection c = Db.get()) {
            c.setAutoCommit(false);

            try {
                // In your schema orders.user_id references users.
                // For ONLINE customer we keep user_id NULL (as we did).
                Order order = new Order(null, customerId, OrderType.ONLINE, paymentMethod, cart.total());
                long orderId = orders.insertOrder(c, order);

                for (CartItem item : cart.items()) {
                    orders.insertOrderItem(c, orderId, item.itemCode(), item.qty(), item.unitPrice());
                    stock.deductWebStockFifo(c, orderId, item.itemCode(), item.qty());
                }

                c.commit();
                return orderId;

            } catch (Exception ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (Exception e) {
            throw new RuntimeException("placeOnlineOrder failed: " + e.getMessage(), e);
        }
    }

    @Override
    public long placePosOrder(long cashierUserId, Cart cart, PaymentMethod paymentMethod) {
        // ✅ 1. Validate cart is not empty
        if (cart == null || cart.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        // ✅ 2. Validate payment method
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method is required");
        }

        // ✅ 3. Check stock availability BEFORE starting transaction
        if (stock instanceof JdbcStockRepository) {
            JdbcStockRepository jdbcStock = (JdbcStockRepository) stock;

            for (CartItem item : cart.items()) {
                int available = jdbcStock.getAvailableShelfStock(item.itemCode());
                if (available < item.qty()) {
                    throw new IllegalStateException(
                        "Insufficient shelf stock for item " + item.itemCode() +
                        ". Requested: " + item.qty() + ", Available: " + available
                    );
                }
            }
        }

        try (Connection c = Db.get()) {
            c.setAutoCommit(false);

            try {
                // PHYSICAL order => user_id = cashierUserId, customerId = null
                Order order = new Order(cashierUserId, null, OrderType.PHYSICAL, paymentMethod, cart.total());
                long orderId = orders.insertOrder(c, order);

                for (CartItem item : cart.items()) {
                    orders.insertOrderItem(c, orderId, item.itemCode(), item.qty(), item.unitPrice());

                    // ✅ POS uses SHELF FIFO deduction
                    stock.deductShelfStockFifo(c, orderId, item.itemCode(), item.qty());
                }

                c.commit();
                return orderId;

            } catch (Exception ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }

        } catch (Exception e) {
            throw new RuntimeException("placePosOrder failed: " + e.getMessage(), e);
        }
    }

}
