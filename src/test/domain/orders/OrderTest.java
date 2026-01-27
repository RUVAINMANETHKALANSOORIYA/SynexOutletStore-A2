package domain.orders;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    @DisplayName("Should create Order and retrieve properties")
    void testOrder() {
        Order order = new Order(1L, 2L, OrderType.ONLINE, PaymentMethod.CARD, 1250.0);
        
        assertEquals(1L, order.userId());
        assertEquals(2L, order.customerId());
        assertEquals(OrderType.ONLINE, order.orderType());
        assertEquals(PaymentMethod.CARD, order.paymentMethod());
        assertEquals(1250.0, order.totalAmount());
    }

    @Test
    @DisplayName("Should allow null IDs")
    void testOrderNullIds() {
        Order order = new Order(null, null, OrderType.PHYSICAL, PaymentMethod.CASH, 500.0);
        
        assertNull(order.userId());
        assertNull(order.customerId());
        assertEquals(OrderType.PHYSICAL, order.orderType());
        assertEquals(PaymentMethod.CASH, order.paymentMethod());
        assertEquals(500.0, order.totalAmount());
    }
}
