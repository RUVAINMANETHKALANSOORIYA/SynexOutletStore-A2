package domain.cart;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Collection;
import static org.junit.jupiter.api.Assertions.*;

class CartTest {
    private Cart cart;

    @BeforeEach
    void setUp() {
        cart = new Cart();
    }

    @Test
    @DisplayName("Should add new items to cart")
    void testAdd() {
        cart.add("ITM001", "Milk", 450.0, 2);
        assertFalse(cart.isEmpty());
        assertEquals(1, cart.items().size());
        assertEquals(900.0, cart.total());
    }

    @Test
    @DisplayName("Should increment quantity for existing items")
    void testAddExisting() {
        cart.add("ITM001", "Milk", 450.0, 2);
        cart.add("ITM001", "Milk", 450.0, 3);
        assertEquals(1, cart.items().size());
        CartItem item = cart.items().iterator().next();
        assertEquals(5, item.qty());
        assertEquals(2250.0, cart.total());
    }

    @Test
    @DisplayName("Should clear cart")
    void testClear() {
        cart.add("ITM001", "Milk", 450.0, 2);
        cart.clear();
        assertTrue(cart.isEmpty());
        assertEquals(0, cart.items().size());
        assertEquals(0.0, cart.total());
    }

    @Test
    @DisplayName("Should return all items")
    void testItems() {
        cart.add("ITM001", "Milk", 450.0, 1);
        cart.add("ITM002", "Bread", 220.0, 1);
        Collection<CartItem> items = cart.items();
        assertEquals(2, items.size());
    }
}
