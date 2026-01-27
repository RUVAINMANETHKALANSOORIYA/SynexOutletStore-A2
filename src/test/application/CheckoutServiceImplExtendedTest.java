package application;

import domain.cart.Cart;
import domain.orders.PaymentMethod;
import fakes.FakeOrderRepository;
import fakes.FakeStockRepository;
import fakes.JdbcFakes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CheckoutServiceImplExtendedTest {

    private CheckoutServiceImpl service;
    private FakeOrderRepository orderRepo;
    private FakeStockRepository stockRepo;

    @BeforeAll
    static void init() throws Exception {
        Class.forName("fakes.JdbcFakes$FakeDriver");
    }

    @BeforeEach
    void setUp() {
        orderRepo = new FakeOrderRepository();
        stockRepo = new FakeStockRepository();
        service = new CheckoutServiceImpl(orderRepo, stockRepo);
    }

    @Test
    @DisplayName("Online order validation")
    void testOnlineOrderValidation() {
        Cart cart = new Cart();
        assertThrows(IllegalArgumentException.class, () -> service.placeOnlineOrder(1, null, PaymentMethod.CARD));
        assertThrows(IllegalArgumentException.class, () -> service.placeOnlineOrder(1, cart, PaymentMethod.CARD));
        
        cart.add("I1", "N1", 10, 1);
        assertThrows(IllegalArgumentException.class, () -> service.placeOnlineOrder(1, cart, null));
    }

    @Test
    @DisplayName("POS order validation")
    void testPosOrderValidation() {
        Cart cart = new Cart();
        assertThrows(IllegalArgumentException.class, () -> service.placePosOrder(1, null, PaymentMethod.CASH));
        assertThrows(IllegalArgumentException.class, () -> service.placePosOrder(1, cart, PaymentMethod.CASH));
        
        cart.add("I1", "N1", 10, 1);
        assertThrows(IllegalArgumentException.class, () -> service.placePosOrder(1, cart, null));
    }

    @Test
    @DisplayName("Online order insufficient stock")
    void testOnlineInsufficientStock() {
        Cart cart = new Cart();
        cart.add("I1", "N1", 10, 10);
        stockRepo.setWebStock("I1", 5);
        assertThrows(RuntimeException.class, () -> service.placeOnlineOrder(1, cart, PaymentMethod.CARD));
    }

    @Test
    @DisplayName("POS order insufficient stock")
    void testPosInsufficientStock() {
        Cart cart = new Cart();
        cart.add("I1", "N1", 10, 10);
        stockRepo.setShelfStock("I1", 5);
        assertThrows(RuntimeException.class, () -> service.placePosOrder(1, cart, PaymentMethod.CASH));
    }

    // Individual tests
    @Test void chk01() { Cart c = new Cart(); c.add("I", "N", 1, 1); stockRepo.setWebStock("I", 1); assertDoesNotThrow(() -> service.placeOnlineOrder(1, c, PaymentMethod.CARD)); }
    @Test void chk02() { Cart c = new Cart(); c.add("I", "N", 1, 1); stockRepo.setShelfStock("I", 1); assertDoesNotThrow(() -> service.placePosOrder(1, c, PaymentMethod.CASH)); }
    @Test void chk03() { Cart c = new Cart(); c.add("I", "N", 1, 1); stockRepo.setWebStock("I", 1); long id = service.placeOnlineOrder(1, c, PaymentMethod.CARD); assertTrue(id > 0); }
    @Test void chk04() { Cart c = new Cart(); c.add("I", "N", 1, 1); stockRepo.setShelfStock("I", 1); long id = service.placePosOrder(1, c, PaymentMethod.CASH); assertTrue(id > 0); }
    @Test void chk05() { Cart c = new Cart(); c.add("I", "N", 1, 2); stockRepo.setWebStock("I", 1); assertThrows(RuntimeException.class, () -> service.placeOnlineOrder(1, c, PaymentMethod.CARD)); }
    @Test void chk06() { Cart c = new Cart(); c.add("I1", "N1", 1, 1); c.add("I2", "N2", 1, 1); stockRepo.setWebStock("I1", 1); stockRepo.setWebStock("I2", 1); assertDoesNotThrow(() -> service.placeOnlineOrder(1, c, PaymentMethod.CARD)); }
    @Test void chk07() { Cart c = new Cart(); c.add("I1", "N1", 1, 1); c.add("I2", "N2", 1, 1); stockRepo.setWebStock("I1", 1); stockRepo.setWebStock("I2", 0); assertThrows(RuntimeException.class, () -> service.placeOnlineOrder(1, c, PaymentMethod.CARD)); }
    @Test void chk08() { Cart c = new Cart(); c.add("I", "N", 1, 1); stockRepo.setWebStock("I", 1); service.placeOnlineOrder(1, c, PaymentMethod.CARD); assertEquals(1, orderRepo.getOrders().size()); }
    @Test void chk09() { Cart c = new Cart(); c.add("I", "N", 1, 1); stockRepo.setWebStock("I", 1); service.placeOnlineOrder(1, c, PaymentMethod.CARD); assertEquals(1, orderRepo.getOrderItems().size()); }
    @Test void chk10() { Cart c = new Cart(); c.add("I", "N", 10.0, 2); stockRepo.setWebStock("I", 2); service.placeOnlineOrder(1, c, PaymentMethod.CARD); assertEquals(20.0, orderRepo.getOrders().get(0).totalAmount()); }
    @Test void chk11() { assertNotNull(service); }
    @Test void chk12() { Cart c = new Cart(); c.add("I", "N", 1, 1); stockRepo.setWebStock("I", 10); service.placeOnlineOrder(1, c, PaymentMethod.CARD); service.placeOnlineOrder(1, c, PaymentMethod.CARD); assertEquals(2, orderRepo.getOrders().size()); }
    @Test void chk13() { Cart c = new Cart(); c.add("I", "N", 1, 1); stockRepo.setShelfStock("I", 10); service.placePosOrder(1, c, PaymentMethod.CASH); service.placePosOrder(1, c, PaymentMethod.CASH); assertEquals(2, orderRepo.getOrders().size()); }
    @Test void chk14() { Cart c = new Cart(); c.add("I", "N", 1, 1); stockRepo.setWebStock("I", 1); service.placeOnlineOrder(1, c, PaymentMethod.CARD); assertThrows(RuntimeException.class, () -> service.placeOnlineOrder(1, c, PaymentMethod.CARD)); }
    @Test void chk15() { Cart c = new Cart(); c.add("I", "N", 1, 1); stockRepo.setShelfStock("I", 1); service.placePosOrder(1, c, PaymentMethod.CASH); assertThrows(RuntimeException.class, () -> service.placePosOrder(1, c, PaymentMethod.CASH)); }
    @Test void chk16() { Cart c = new Cart(); c.add("I", "N", 1, 1); stockRepo.setWebStock("I", 1); service.placeOnlineOrder(1, c, PaymentMethod.CARD); assertEquals(0, stockRepo.getAvailableWebStock("I")); }
    @Test void chk17() { Cart c = new Cart(); c.add("I", "N", 1, 1); stockRepo.setShelfStock("I", 1); service.placePosOrder(1, c, PaymentMethod.CASH); assertEquals(0, stockRepo.getAvailableShelfStock("I")); }
    @Test void chk18() { Cart c = new Cart(); c.add("I", "N", 1, 5); stockRepo.addBatch("I", java.time.LocalDate.now(), 3, 0); stockRepo.addBatch("I", java.time.LocalDate.now().plusDays(1), 3, 0); assertDoesNotThrow(() -> service.placeOnlineOrder(1, c, PaymentMethod.CARD)); }
    @Test void chk19() { Cart c = new Cart(); c.add("I", "N", 1, 5); stockRepo.addBatch("I", java.time.LocalDate.now(), 3, 0); stockRepo.addBatch("I", java.time.LocalDate.now().plusDays(1), 1, 0); assertThrows(RuntimeException.class, () -> service.placeOnlineOrder(1, c, PaymentMethod.CARD)); }
    @Test void chk20() { Cart c = new Cart(); c.add("I", "N", 1, 1); stockRepo.setWebStock("I", 1); service.placeOnlineOrder(1, c, PaymentMethod.CARD); assertTrue(orderRepo.getOrders().get(0).orderType() == domain.orders.OrderType.ONLINE); }
    @Test void chk21() { Cart c = new Cart(); c.add("I", "N", 1, 1); stockRepo.setShelfStock("I", 1); service.placePosOrder(1, c, PaymentMethod.CASH); assertTrue(orderRepo.getOrders().get(0).orderType() == domain.orders.OrderType.PHYSICAL); }
    @Test void chk22() { Cart c = new Cart(); c.add("I", "N", 1, 1); stockRepo.setWebStock("I", 1); service.placeOnlineOrder(1, c, PaymentMethod.CARD); assertEquals(1L, orderRepo.getOrders().get(0).customerId()); }
    @Test void chk23() { Cart c = new Cart(); c.add("I", "N", 1, 1); stockRepo.setShelfStock("I", 1); service.placePosOrder(123L, c, PaymentMethod.CASH); assertEquals(123L, orderRepo.getOrders().get(0).userId()); }
    @Test void chk24() { Cart c = new Cart(); c.add("I", "N", 1, 1); stockRepo.setWebStock("I", 1); service.placeOnlineOrder(1, c, PaymentMethod.CARD); assertNull(orderRepo.getOrders().get(0).userId()); }
    @Test void chk25() { Cart c = new Cart(); c.add("I", "N", 1, 1); stockRepo.setShelfStock("I", 1); service.placePosOrder(1, c, PaymentMethod.CASH); assertNull(orderRepo.getOrders().get(0).customerId()); }
    @Test void chk26() { Cart c = new Cart(); c.add("I", "N", 5.5, 2); stockRepo.setWebStock("I", 2); service.placeOnlineOrder(1, c, PaymentMethod.CARD); assertEquals(11.0, orderRepo.getOrders().get(0).totalAmount()); }
    @Test void chk27() { Cart c = new Cart(); c.add("I", "N", 1, 1); stockRepo.setWebStock("I", 1); service.placeOnlineOrder(1, c, PaymentMethod.CARD); assertEquals(PaymentMethod.CARD, orderRepo.getOrders().get(0).paymentMethod()); }
    @Test void chk28() { Cart c = new Cart(); c.add("I", "N", 1, 1); stockRepo.setShelfStock("I", 1); service.placePosOrder(1, c, PaymentMethod.CASH); assertEquals(PaymentMethod.CASH, orderRepo.getOrders().get(0).paymentMethod()); }
    @Test void chk29() { Cart c = new Cart(); c.add("I", "N", 1, 1); stockRepo.setWebStock("I", 1); service.placeOnlineOrder(1, c, PaymentMethod.CARD); assertEquals("I", orderRepo.getOrderItems().get(0).itemCode()); }
    @Test void chk30() { Cart c = new Cart(); c.add("I", "N", 1, 1); stockRepo.setWebStock("I", 1); service.placeOnlineOrder(1, c, PaymentMethod.CARD); assertEquals(1, orderRepo.getOrderItems().get(0).qty()); }
}
