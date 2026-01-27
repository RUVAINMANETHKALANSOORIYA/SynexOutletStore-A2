package domain.orders;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class OrderExtendedTest {

    @Test
    @DisplayName("Order properties")
    void testOrder() {
        Order order = new Order(10L, 20L, OrderType.ONLINE, PaymentMethod.CARD, 1500.0);
        assertEquals(10L, order.userId());
        assertEquals(20L, order.customerId());
        assertEquals(OrderType.ONLINE, order.orderType());
        assertEquals(PaymentMethod.CARD, order.paymentMethod());
        assertEquals(1500.0, order.totalAmount());
    }

    @Test
    @DisplayName("Order null fields")
    void testOrderNulls() {
        Order order = new Order(null, null, null, null, 0.0);
        assertNull(order.userId());
        assertNull(order.customerId());
        assertNull(order.orderType());
        assertNull(order.paymentMethod());
        assertEquals(0.0, order.totalAmount());
    }

    // Individual tests
    @Test void order01() { assertNotNull(new Order(1L, null, OrderType.PHYSICAL, PaymentMethod.CASH, 100.0)); }
    @Test void order02() { assertNotNull(new Order(null, 1L, OrderType.ONLINE, PaymentMethod.CARD, 100.0)); }
    @Test void order03() { assertEquals(100.0, new Order(1L, null, OrderType.PHYSICAL, PaymentMethod.CASH, 100.0).totalAmount()); }
    @Test void order04() { assertEquals(PaymentMethod.CASH, new Order(1L, null, OrderType.PHYSICAL, PaymentMethod.CASH, 100.0).paymentMethod()); }
    @Test void order05() { assertEquals(OrderType.PHYSICAL, new Order(1L, null, OrderType.PHYSICAL, PaymentMethod.CASH, 100.0).orderType()); }
    @Test void order06() { assertEquals(1L, new Order(1L, null, OrderType.PHYSICAL, PaymentMethod.CASH, 100.0).userId()); }
    @Test void order07() { assertNull(new Order(1L, null, OrderType.PHYSICAL, PaymentMethod.CASH, 100.0).customerId()); }
    @Test void order08() { assertNotNull(new Order(1L, null, OrderType.PHYSICAL, PaymentMethod.CASH, 100.0).toString()); }
    @Test void order09() { Order o = new Order(1L, 2L, OrderType.ONLINE, PaymentMethod.CARD, 1.0); assertNotNull(o); }
    @Test void order10() { Order o = new Order(1L, 2L, OrderType.ONLINE, PaymentMethod.CARD, 1.0); assertEquals(o.customerId(), 2L); }
    @Test void order11() { for (OrderType t : OrderType.values()) assertNotNull(t); }
    @Test void order12() { for (PaymentMethod p : PaymentMethod.values()) assertNotNull(p); }
    @Test void order13() { assertEquals("ONLINE", OrderType.ONLINE.name()); }
    @Test void order14() { assertEquals("PHYSICAL", OrderType.PHYSICAL.name()); }
    @Test void order15() { assertEquals("CASH", PaymentMethod.CASH.name()); }
    @Test void order16() { assertEquals("CARD", PaymentMethod.CARD.name()); }
    @Test void order17() { assertNotNull(OrderType.valueOf("ONLINE")); }
    @Test void order18() { assertNotNull(PaymentMethod.valueOf("CARD")); }
    @Test void order19() { Order o = new Order(0L, 0L, OrderType.ONLINE, PaymentMethod.CASH, -1.0); assertEquals(-1.0, o.totalAmount()); }
    @Test void order20() { assertNotNull(new Order(999L, 999L, OrderType.PHYSICAL, PaymentMethod.CARD, 999.99)); }
    @Test void order21() { Order o1 = new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 10.0); Order o2 = new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 10.0); assertEquals(o1, o2); }
    @Test void order22() { Order o1 = new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 10.0); assertEquals(o1.hashCode(), o1.hashCode()); }
    @Test void order23() { Order o1 = new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 10.0); assertNotEquals(o1, null); }
    @Test void order24() { Order o1 = new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 10.0); assertNotEquals(o1, "not an order"); }
    @Test void order25() { Order o1 = new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 10.0); assertEquals(o1, o1); }
    @Test void order26() { assertNotNull(OrderType.ONLINE); }
    @Test void order27() { assertNotNull(OrderType.PHYSICAL); }
    @Test void order28() { assertNotNull(PaymentMethod.CARD); }
    @Test void order29() { assertNotNull(PaymentMethod.CASH); }
    @Test void order30() { Order o = new Order(1L, null, OrderType.PHYSICAL, PaymentMethod.CASH, 0); assertEquals(0, o.totalAmount()); }
}
