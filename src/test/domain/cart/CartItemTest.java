package domain.cart;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CartItemTest {

    @Test
    @DisplayName("Should create CartItem and calculate line total correctly")
    void testCartItem() {
        CartItem item = new CartItem("ITM001", "Milk", 450.0, 3);
        
        assertEquals("ITM001", item.itemCode());
        assertEquals("Milk", item.name());
        assertEquals(450.0, item.unitPrice());
        assertEquals(3, item.qty());
        assertEquals(1350.0, item.lineTotal());
    }
}
