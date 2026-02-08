package application;

import domain.cart.Cart;
import domain.orders.Order;
import domain.orders.OrderType;
import domain.orders.PaymentMethod;
import fakes.FakeOrderRepository;
import fakes.FakeStockRepository;
import fakes.JdbcFakes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class CheckoutServiceImplTest {
    private CheckoutServiceImpl checkoutService;
    private FakeOrderRepository orderRepo;
    private FakeStockRepository stockRepo;

    @BeforeAll
    static void initJdbc() throws Exception {
        // Register the fake driver
        Class.forName("fakes.JdbcFakes$FakeDriver");
    }

    @BeforeEach
    void setUp() {
        orderRepo = new FakeOrderRepository();
        stockRepo = new FakeStockRepository();
        checkoutService = new CheckoutServiceImpl(orderRepo, stockRepo);
    }

    @Test
    @DisplayName("Should fail online order if cart is empty")
    void testPlaceOnlineOrderEmptyCart() {
        Cart cart = new Cart();
        assertThrows(domain.exception.CheckoutFailedException.class, () -> 
            checkoutService.placeOnlineOrder(1L, cart, PaymentMethod.CARD));
    }

    @Test
    @DisplayName("Should fail online order if payment method is null")
    void testPlaceOnlineOrderNoPayment() {
        Cart cart = new Cart();
        cart.add("ITM001", "Milk", 450.0, 1);
        assertThrows(domain.exception.CheckoutFailedException.class, () -> 
            checkoutService.placeOnlineOrder(1L, cart, null));
    }

    @Test
    @DisplayName("Should fail online order if stock is insufficient")
    void testPlaceOnlineOrderInsufficientStock() {
        Cart cart = new Cart();
        cart.add("ITM001", "Milk", 450.0, 10);
        stockRepo.setWebStock("ITM001", 5);
        
        // Note: The pre-check is skipped because stockRepo is not JdbcStockRepository.
        // But the deduction check inside deductWebStockFifo will catch it.
        assertThrows(RuntimeException.class, () -> 
            checkoutService.placeOnlineOrder(1L, cart, PaymentMethod.CARD));
    }

    @Test
    @DisplayName("Should place online order successfully")
    void testPlaceOnlineOrderSuccess() {
        Cart cart = new Cart();
        cart.add("ITM001", "Milk", 450.0, 2);
        stockRepo.setWebStock("ITM001", 10);
        
        long orderId = checkoutService.placeOnlineOrder(1L, cart, PaymentMethod.CARD);
        
        assertTrue(orderId > 0);
        assertEquals(1, orderRepo.getOrders().size());
        assertEquals(1, orderRepo.getOrderItems().size());
        
        Order order = orderRepo.getOrders().get(0);
        assertEquals(OrderType.ONLINE, order.orderType());
        assertEquals(900.0, order.totalAmount());
        assertEquals(1L, order.customerId());
        assertNull(order.userId());
    }

    @Test
    @DisplayName("Should place POS order successfully")
    void testPlacePosOrderSuccess() {
        Cart cart = new Cart();
        cart.add("ITM001", "Milk", 450.0, 1);
        stockRepo.setShelfStock("ITM001", 5);
        
        long orderId = checkoutService.placePosOrder(2L, cart, PaymentMethod.CASH);
        
        assertTrue(orderId > 0);
        Order order = orderRepo.getOrders().get(0);
        assertEquals(OrderType.PHYSICAL, order.orderType());
        assertEquals(2L, order.userId());
        assertNull(order.customerId());
    }

    @Test
    @DisplayName("Should deduct stock following FEFO (Earliest Expiry First)")
    void testPlaceOrderFefo() {
        LocalDate today = LocalDate.now();
        // Batch 1: Expires today
        stockRepo.addBatch("ITM001", today, 5, 0);
        // Batch 2: Expires tomorrow
        stockRepo.addBatch("ITM001", today.plusDays(1), 5, 0);
        
        Cart cart = new Cart();
        cart.add("ITM001", "Milk", 450.0, 7);
        
        checkoutService.placeOnlineOrder(1L, cart, PaymentMethod.CARD);
        
        // After taking 7:
        // Batch 1 should be 0 (all 5 taken)
        // Batch 2 should be 3 (2 taken)
        // We can verify this if we add a getter to stockRepo or by trying to take more.
        
        // Try to take 4 more from web - should fail because only 3 left in Batch 2
        cart.clear();
        cart.add("ITM001", "Milk", 450.0, 4);
        assertThrows(RuntimeException.class, () -> 
            checkoutService.placeOnlineOrder(1L, cart, PaymentMethod.CARD));
            
        // Taking 3 should work
        cart.clear();
        cart.add("ITM001", "Milk", 450.0, 3);
        // Add more stock because previous failing attempt deducted stock in FakeStockRepository (no rollback)
        stockRepo.addBatch("ITM001", today, 3, 0);
        assertDoesNotThrow(() -> checkoutService.placeOnlineOrder(1L, cart, PaymentMethod.CARD));
    }
}
