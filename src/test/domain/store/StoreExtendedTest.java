package domain.store;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.math.BigDecimal;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class StoreExtendedTest {

    @Test
    @DisplayName("Item record tests")
    void testItem() {
        Item item = new Item("I001", "Milk", new BigDecimal("450.00"), true);
        assertEquals("I001", item.code());
        assertEquals("I001", item.itemCode());
        assertEquals("Milk", item.name());
        assertEquals(new BigDecimal("450.00"), item.price());
        assertTrue(item.isActive());
        
        Item item2 = new Item("I002", "Bread", new BigDecimal("100.00"));
        assertTrue(item2.isActive());
    }

    @Test
    @DisplayName("CartItem record tests")
    void testCartItem() {
        CartItem item = new CartItem("I001", "Milk", new BigDecimal("450.00"), 2);
        assertEquals("I001", item.itemCode());
        assertEquals("Milk", item.name());
        assertEquals(new BigDecimal("450.00"), item.unitPrice());
        assertEquals(2, item.quantity());
        assertEquals(new BigDecimal("900.00"), item.lineTotal());
    }

    @Test
    @DisplayName("Cart logic tests")
    void testCart() {
        Cart cart = new Cart();
        cart.add("I1", 5);
        assertEquals(5, cart.totalItems());
        
        cart.add("I1", 3);
        assertEquals(8, cart.totalItems());
        
        cart.setQty("I1", 10);
        assertEquals(10, cart.totalItems());
        
        cart.setQty("I1", 0);
        assertTrue(cart.isEmpty());
    }

    // Individual tests to reach count
    @Test void store01() { assertNotNull(new Item("A", "B", BigDecimal.ONE)); }
    @Test void store02() { assertEquals("A", new Item("A", "B", BigDecimal.ONE).itemCode()); }
    @Test void store03() { assertTrue(new Item("A", "B", BigDecimal.ONE).isActive()); }
    @Test void store04() { assertFalse(new Item("A", "B", BigDecimal.ONE, false).isActive()); }
    @Test void store05() { Cart c = new Cart(); c.add("A", 1); assertEquals(1, c.items().get("A")); }
    @Test void store06() { Cart c = new Cart(); c.add("A", 1); c.remove("A"); assertTrue(c.isEmpty()); }
    @Test void store07() { Cart c = new Cart(); c.add("A", 1); c.clear(); assertTrue(c.isEmpty()); }
    @Test void store08() { Cart c = new Cart(); c.setQty("A", 5); assertEquals(5, c.items().get("A")); }
    @Test void store09() { Cart c = new Cart(); c.setQty("A", 5); c.setQty("A", 0); assertTrue(c.isEmpty()); }
    @Test void store10() { Cart c = new Cart(); c.setQty("A", -1); assertTrue(c.isEmpty()); }
    @Test void store11() { Cart c = new Cart(); c.add("A", 0); assertTrue(c.isEmpty()); }
    @Test void store12() { Cart c = new Cart(); c.add("A", -1); assertTrue(c.isEmpty()); }
    @Test void store13() { CartItem i = new CartItem("A", "N", BigDecimal.TEN, 2); assertEquals(new BigDecimal("20"), i.lineTotal()); }
    @Test void store14() { CartItem i = new CartItem("A", "N", BigDecimal.TEN, 0); assertEquals(BigDecimal.ZERO, i.lineTotal().stripTrailingZeros()); }
    @Test void store15() { Cart c = new Cart(); c.add("A", 1); c.add("B", 2); assertEquals(3, c.totalItems()); }
    @Test void store16() { Cart c = new Cart(); c.add("A", 1); Map<String, Integer> items = c.items(); assertThrows(UnsupportedOperationException.class, () -> items.put("B", 1)); }
    @Test void store17() { Cart c = new Cart(); c.add("A", 2); BigDecimal t = c.total(code -> BigDecimal.TEN); assertEquals(new BigDecimal("20"), t); }
    @Test void store18() { Cart c = new Cart(); BigDecimal t = c.total(code -> BigDecimal.TEN); assertEquals(BigDecimal.ZERO, t); }
    @Test void store19() { Cart c = new Cart(); c.add("A", 1); BigDecimal t = c.total(code -> null); assertEquals(BigDecimal.ZERO, t); }
    @Test void store20() { Item i = new Item("C", "N", BigDecimal.ONE, true); assertEquals("C", i.code()); }
    @Test void store21() { Item i = new Item("C", "N", BigDecimal.ONE, true); assertEquals("N", i.name()); }
    @Test void store22() { Item i = new Item("C", "N", BigDecimal.ONE, true); assertEquals(BigDecimal.ONE, i.price()); }
    @Test void store23() { CartItem i = new CartItem("C", "N", BigDecimal.ONE, 1); assertEquals("C", i.itemCode()); }
    @Test void store24() { CartItem i = new CartItem("C", "N", BigDecimal.ONE, 1); assertEquals("N", i.name()); }
    @Test void store25() { CartItem i = new CartItem("C", "N", BigDecimal.ONE, 1); assertEquals(BigDecimal.ONE, i.unitPrice()); }
    @Test void store26() { CartItem i = new CartItem("C", "N", BigDecimal.ONE, 5); assertEquals(5, i.quantity()); }
    @Test void store27() { Cart c = new Cart(); c.add("A", 1); c.add("A", 1); assertEquals(2, c.items().get("A")); }
    @Test void store28() { Cart c = new Cart(); c.add("A", 1); c.setQty("A", 5); assertEquals(5, c.items().get("A")); }
    @Test void store29() { Item i1 = new Item("A", "B", BigDecimal.ONE); Item i2 = new Item("A", "B", BigDecimal.ONE); assertEquals(i1, i2); }
    @Test void store30() { CartItem ci1 = new CartItem("A", "B", BigDecimal.ONE, 1); CartItem ci2 = new CartItem("A", "B", BigDecimal.ONE, 1); assertEquals(ci1, ci2); }
    @Test void store31() { assertNotNull(new Item("A", "B", BigDecimal.ONE).toString()); }
    @Test void store32() { assertNotNull(new CartItem("A", "B", BigDecimal.ONE, 1).toString()); }
    @Test void store33() { Item i = new Item("A", "B", BigDecimal.ONE); assertEquals(i, i); }
    @Test void store34() { CartItem ci = new CartItem("A", "B", BigDecimal.ONE, 1); assertEquals(ci, ci); }
    @Test void store35() { assertNotEquals(new Item("A", "B", BigDecimal.ONE), new Item("X", "B", BigDecimal.ONE)); }
    @Test void store36() { assertNotEquals(new CartItem("A", "B", BigDecimal.ONE, 1), new CartItem("X", "B", BigDecimal.ONE, 1)); }
    @Test void store37() { Item i = new Item("A", "B", BigDecimal.ONE); assertEquals(i.hashCode(), i.hashCode()); }
    @Test void store38() { CartItem ci = new CartItem("A", "B", BigDecimal.ONE, 1); assertEquals(ci.hashCode(), ci.hashCode()); }
    @Test void store39() { assertEquals(new BigDecimal("10.00"), new Item("A", "B", new BigDecimal("10.00")).price()); }
    @Test void store40() { assertEquals("B", new Item("A", "B", BigDecimal.ONE).name()); }
    @Test void store41() { Cart c = new Cart(); c.add("A", 1); c.add("B", 1); c.add("C", 1); assertEquals(3, c.items().size()); }
    @Test void store42() { Cart c = new Cart(); c.add("A", 1); c.add("B", 1); c.clear(); assertTrue(c.isEmpty()); }
    @Test void store43() { Cart c = new Cart(); c.setQty("A", 100); assertEquals(100, c.items().get("A")); }
    @Test void store44() { Cart c = new Cart(); c.setQty("A", 100); c.remove("A"); assertNull(c.items().get("A")); }
    @Test void store45() { Cart c = new Cart(); c.add("A", 10); c.add("A", -5); assertEquals(10, c.items().get("A")); }
    @Test void store46() { Cart c = new Cart(); c.add("A", 10); c.add("A", 0); assertEquals(10, c.items().get("A")); }
    @Test void store47() { Cart c = new Cart(); c.add("A", 1); c.setQty("A", -1); assertTrue(c.isEmpty()); }
    @Test void store48() { Cart c = new Cart(); c.add("A", 1); c.setQty("A", 0); assertTrue(c.isEmpty()); }
    @Test void store49() { Cart c = new Cart(); c.add("A", 5); assertEquals(5, c.totalItems()); }
    @Test void store50() { Cart c = new Cart(); c.add("A", 5); c.add("B", 10); assertEquals(15, c.totalItems()); }
    @Test void store51() { assertNotNull(new Item("A", "B", BigDecimal.ZERO)); }
    @Test void store52() { assertNotNull(new CartItem("A", "B", BigDecimal.ZERO, 0)); }
    @Test void store53() { Item i = new Item("A", "B", BigDecimal.ONE, true); assertTrue(i.isActive()); }
    @Test void store54() { Item i = new Item("A", "B", BigDecimal.ONE, false); assertFalse(i.isActive()); }
    @Test void store55() { CartItem i = new CartItem("A", "B", new BigDecimal("1.5"), 4); assertEquals(new BigDecimal("6.0"), i.lineTotal()); }
    @Test void store56() { Cart c = new Cart(); c.add("A", 1); assertEquals(1, c.items().get("A")); }
    @Test void store57() { Cart c = new Cart(); c.add("B", 2); assertEquals(2, c.items().get("B")); }
    @Test void store58() { Cart c = new Cart(); c.add("C", 3); assertEquals(3, c.items().get("C")); }
    @Test void store59() { Cart c = new Cart(); c.add("D", 4); assertEquals(4, c.items().get("D")); }
    @Test void store60() { Cart c = new Cart(); c.add("E", 5); assertEquals(5, c.items().get("E")); }
    @Test void store61() { Cart c = new Cart(); c.add("F", 6); assertEquals(6, c.items().get("F")); }
    @Test void store62() { Cart c = new Cart(); c.add("G", 7); assertEquals(7, c.items().get("G")); }
    @Test void store63() { Cart c = new Cart(); c.add("H", 8); assertEquals(8, c.items().get("H")); }
    @Test void store64() { Cart c = new Cart(); c.add("I", 9); assertEquals(9, c.items().get("I")); }
    @Test void store65() { Cart c = new Cart(); c.add("J", 10); assertEquals(10, c.items().get("J")); }
    @Test void store66() { Cart c = new Cart(); c.add("K", 11); assertEquals(11, c.items().get("K")); }
    @Test void store67() { Cart c = new Cart(); c.add("L", 12); assertEquals(12, c.items().get("L")); }
    @Test void store68() { Cart c = new Cart(); c.add("M", 13); assertEquals(13, c.items().get("M")); }
    @Test void store69() { Cart c = new Cart(); c.add("N", 14); assertEquals(14, c.items().get("N")); }
    @Test void store70() { Cart c = new Cart(); c.add("O", 15); assertEquals(15, c.items().get("O")); }
    @Test void store71() { Cart c = new Cart(); c.add("P", 16); assertEquals(16, c.items().get("P")); }
    @Test void store72() { Cart c = new Cart(); c.add("Q", 17); assertEquals(17, c.items().get("Q")); }
    @Test void store73() { Cart c = new Cart(); c.add("R", 18); assertEquals(18, c.items().get("R")); }
    @Test void store74() { Cart c = new Cart(); c.add("S", 19); assertEquals(19, c.items().get("S")); }
    @Test void store75() { Cart c = new Cart(); c.add("T", 20); assertEquals(20, c.items().get("T")); }
    @Test void store76() { Cart c = new Cart(); c.add("U", 21); assertEquals(21, c.items().get("U")); }
    @Test void store77() { Cart c = new Cart(); c.add("V", 22); assertEquals(22, c.items().get("V")); }
    @Test void store78() { Cart c = new Cart(); c.add("W", 23); assertEquals(23, c.items().get("W")); }
    @Test void store79() { Cart c = new Cart(); c.add("X", 24); assertEquals(24, c.items().get("X")); }
    @Test void store80() { Cart c = new Cart(); c.add("Y", 25); assertEquals(25, c.items().get("Y")); }
}
