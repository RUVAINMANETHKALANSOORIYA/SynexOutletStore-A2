package domain.store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class StoreCartTest {
    private Cart cart;

    @BeforeEach
    void setUp() {
        cart = new Cart();
    }

    @Test
    @DisplayName("Should add items to store cart")
    void testAdd() {
        cart.add("ITM001", 2);
        assertFalse(cart.isEmpty());
        assertEquals(2, cart.totalItems());
        assertEquals(1, cart.items().size());
        assertEquals(2, cart.items().get("ITM001"));
    }

    @Test
    @DisplayName("Should handle adding zero or negative quantities")
    void testAddEdgeCases() {
        cart.add("ITM001", 0);
        assertTrue(cart.isEmpty());
        
        cart.add("ITM001", 5);
        cart.add("ITM001", -5); // Does nothing in code
        assertFalse(cart.isEmpty());
        assertEquals(5, cart.items().get("ITM001"));
        
        cart.setQty("ITM001", 0);
        assertTrue(cart.isEmpty());
    }

    @Test
    @DisplayName("Should set quantity explicitly")
    void testSetQty() {
        cart.setQty("ITM001", 10);
        assertEquals(10, cart.items().get("ITM001"));
        
        cart.setQty("ITM001", 0);
        assertFalse(cart.items().containsKey("ITM001"));
    }

    @Test
    @DisplayName("Should remove items")
    void testRemove() {
        cart.add("ITM001", 5);
        cart.remove("ITM001");
        assertTrue(cart.isEmpty());
    }

    @Test
    @DisplayName("Should clear cart")
    void testClear() {
        cart.add("ITM001", 5);
        cart.add("ITM002", 3);
        cart.clear();
        assertTrue(cart.isEmpty());
    }

    @Test
    @DisplayName("Should calculate total with price lookup")
    void testTotal() {
        cart.add("ITM001", 2);
        cart.add("ITM002", 1);
        
        BigDecimal total = cart.total(code -> {
            if ("ITM001".equals(code)) return new BigDecimal("450.00");
            if ("ITM002".equals(code)) return new BigDecimal("220.00");
            return null;
        });
        
        assertEquals(new BigDecimal("1120.00"), total);
    }
}
