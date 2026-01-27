package domain.store;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    @DisplayName("Should create Item and retrieve properties")
    void testItem() {
        BigDecimal price = new BigDecimal("450.00");
        Item item = new Item("ITM001", "Milk", price, true);
        
        assertEquals("ITM001", item.code());
        assertEquals("ITM001", item.itemCode());
        assertEquals("Milk", item.name());
        assertEquals(price, item.price());
        assertTrue(item.isActive());
    }

    @Test
    @DisplayName("Should use default active status in convenience constructor")
    void testItemConvenienceConstructor() {
        BigDecimal price = new BigDecimal("220.00");
        Item item = new Item("ITM002", "Bread", price);
        
        assertTrue(item.isActive());
    }
}
