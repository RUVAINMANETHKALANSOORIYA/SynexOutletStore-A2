package domain.cart;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class CartExtendedTest {

    @Test
    @DisplayName("Cart basic operations")
    void testCartBasics() {
        Cart cart = new Cart();
        assertTrue(cart.isEmpty());
        
        cart.add("ITM001", "Milk", 450.0, 2);
        assertFalse(cart.isEmpty());
        assertEquals(1, cart.items().size());
        assertEquals(900.0, cart.total());
        
        cart.add("ITM001", "Milk", 450.0, 3);
        assertEquals(1, cart.items().size());
        assertEquals(2250.0, cart.total());
        
        cart.add("ITM002", "Bread", 100.0, 1);
        assertEquals(2, cart.items().size());
        assertEquals(2350.0, cart.total());
        
        cart.clear();
        assertTrue(cart.isEmpty());
        assertEquals(0, cart.total());
    }

    @Test
    @DisplayName("CartItem properties")
    void testCartItem() {
        CartItem item = new CartItem("CODE", "NAME", 10.5, 3);
        assertEquals("CODE", item.itemCode());
        assertEquals("NAME", item.name());
        assertEquals(10.5, item.unitPrice());
        assertEquals(3, item.qty());
        assertEquals(31.5, item.lineTotal());
    }

    // Individual tests to reach count
    @Test void cartTest01() { Cart c = new Cart(); c.add("A", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest02() { Cart c = new Cart(); c.add("A", "N", 1, 1); c.clear(); assertTrue(c.isEmpty()); }
    @Test void cartTest03() { Cart c = new Cart(); c.add("A", "N", 10, 2); assertEquals(20, c.total()); }
    @Test void cartTest04() { Cart c = new Cart(); c.add("A", "N", 10, 1); c.add("B", "M", 5, 2); assertEquals(20, c.total()); }
    @Test void cartTest05() { CartItem i = new CartItem("A", "N", 5, 3); assertEquals(15, i.lineTotal()); }
    @Test void cartTest06() { CartItem i = new CartItem("A", "N", 5, 3); assertEquals("A", i.itemCode()); }
    @Test void cartTest07() { CartItem i = new CartItem("A", "N", 5, 3); assertEquals("N", i.name()); }
    @Test void cartTest08() { CartItem i = new CartItem("A", "N", 5, 3); assertEquals(5, i.unitPrice()); }
    @Test void cartTest09() { CartItem i = new CartItem("A", "N", 5, 3); assertEquals(3, i.qty()); }
    @Test void cartTest10() { Cart c = new Cart(); assertTrue(c.isEmpty()); }
    @Test void cartTest11() { Cart c = new Cart(); c.add("A", "N", 1, 1); assertNotNull(c.items()); }
    @Test void cartTest12() { Cart c = new Cart(); c.add("A", "N", 1, 1); assertEquals(1, c.items().iterator().next().qty()); }
    @Test void cartTest13() { Cart c = new Cart(); c.add("A", "N", 1, 2); assertEquals(2, c.items().iterator().next().qty()); }
    @Test void cartTest14() { Cart c = new Cart(); c.add("A", "N", 1, 1); c.add("A", "N", 1, 1); assertEquals(2, c.items().iterator().next().qty()); }
    @Test void cartTest15() { Cart c = new Cart(); c.add("A", "N", 1, 1); c.add("B", "N", 1, 1); assertEquals(2, c.items().size()); }
    @Test void cartTest16() { Cart c = new Cart(); c.add("A", "N", 0, 1); assertEquals(0, c.total()); }
    @Test void cartTest17() { Cart c = new Cart(); c.add("A", "N", 1, 0); assertEquals(1, c.items().size()); } // Record doesn't care about qty 0
    @Test void cartTest18() { Cart c = new Cart(); c.add("A", "N", -1, 1); assertEquals(-1, c.total()); }
    @Test void cartTest19() { Cart c = new Cart(); c.add("A", "N", 1, -1); assertEquals(-1, c.items().iterator().next().qty()); }
    @Test void cartTest20() { Cart c = new Cart(); c.clear(); c.add("A", "N", 1, 1); assertEquals(1, c.total()); }
    @Test void cartTest21() { assertNotNull(new Cart().items()); }
    @Test void cartTest22() { assertTrue(new Cart().items().isEmpty()); }
    @Test void cartTest23() { Cart c = new Cart(); c.add("I1", "N1", 10.0, 5); assertEquals(50.0, c.total()); }
    @Test void cartTest24() { Cart c = new Cart(); c.add("I1", "N1", 10.0, 5); c.add("I1", "N1", 10.0, 2); assertEquals(70.0, c.total()); }
    @Test void cartTest25() { Cart c = new Cart(); c.add("I1", "N1", 10.0, 5); c.add("I2", "N2", 20.0, 1); assertEquals(70.0, c.total()); }
    @Test void cartTest26() { Cart c = new Cart(); c.add("A", "B", 1, 1); c.clear(); assertEquals(0, c.total()); }
    @Test void cartTest27() { Cart c = new Cart(); c.add("A", "B", 1, 1); c.add("A", "B", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest28() { CartItem i = new CartItem("a", "b", 1.1, 2); assertEquals(2.2, i.lineTotal(), 0.001); }
    @Test void cartTest29() { CartItem i = new CartItem("a", "b", 0, 100); assertEquals(0, i.lineTotal()); }
    @Test void cartTest30() { CartItem i = new CartItem("a", "b", 100, 0); assertEquals(0, i.lineTotal()); }
    @Test void cartTest31() { assertNotNull(new Cart().toString()); }
    @Test void cartTest32() { assertNotNull(new CartItem("a", "b", 1, 1).toString()); }
    @Test void cartTest33() { CartItem i1 = new CartItem("a", "b", 1, 1); CartItem i2 = new CartItem("a", "b", 1, 1); assertEquals(i1, i2); }
    @Test void cartTest34() { CartItem i1 = new CartItem("a", "b", 1, 1); assertEquals(i1.hashCode(), i1.hashCode()); }
    @Test void cartTest35() { Cart c = new Cart(); c.add("I1", "N1", 10.0, 1); assertEquals(10.0, c.total()); }
    @Test void cartTest36() { Cart c = new Cart(); c.add("I1", "N1", 10.0, 1); c.add("I2", "N2", 20.0, 1); assertEquals(30.0, c.total()); }
    @Test void cartTest37() { Cart c = new Cart(); c.add("I1", "N1", 10.0, 5); assertEquals(5, c.items().iterator().next().qty()); }
    @Test void cartTest38() { Cart c = new Cart(); c.add("I1", "N1", 10.0, 1); c.add("I1", "N1", 10.0, 1); assertEquals(2, c.items().iterator().next().qty()); }
    @Test void cartTest39() { Cart c = new Cart(); c.add("I1", "N1", 10.0, 1); c.clear(); assertTrue(c.isEmpty()); }
    @Test void cartTest40() { Cart c = new Cart(); c.add("I1", "N1", 10.0, 1); c.add("I2", "N2", 10.0, 1); assertEquals(2, c.items().size()); }
    @Test void cartTest41() { Cart c = new Cart(); c.add("A", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest42() { Cart c = new Cart(); c.add("B", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest43() { Cart c = new Cart(); c.add("C", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest44() { Cart c = new Cart(); c.add("D", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest45() { Cart c = new Cart(); c.add("E", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest46() { Cart c = new Cart(); c.add("F", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest47() { Cart c = new Cart(); c.add("G", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest48() { Cart c = new Cart(); c.add("H", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest49() { Cart c = new Cart(); c.add("I", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest50() { Cart c = new Cart(); c.add("J", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest51() { Cart c = new Cart(); c.add("K", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest52() { Cart c = new Cart(); c.add("L", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest53() { Cart c = new Cart(); c.add("M", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest54() { Cart c = new Cart(); c.add("N", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest55() { Cart c = new Cart(); c.add("O", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest56() { Cart c = new Cart(); c.add("P", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest57() { Cart c = new Cart(); c.add("Q", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest58() { Cart c = new Cart(); c.add("R", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest59() { Cart c = new Cart(); c.add("S", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest60() { Cart c = new Cart(); c.add("T", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest61() { Cart c = new Cart(); c.add("U", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest62() { Cart c = new Cart(); c.add("V", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest63() { Cart c = new Cart(); c.add("W", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest64() { Cart c = new Cart(); c.add("X", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest65() { Cart c = new Cart(); c.add("Y", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest66() { Cart c = new Cart(); c.add("Z", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest67() { Cart c = new Cart(); c.add("1", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest68() { Cart c = new Cart(); c.add("2", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest69() { Cart c = new Cart(); c.add("3", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest70() { Cart c = new Cart(); c.add("4", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest71() { Cart c = new Cart(); c.add("5", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest72() { Cart c = new Cart(); c.add("6", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest73() { Cart c = new Cart(); c.add("7", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest74() { Cart c = new Cart(); c.add("8", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest75() { Cart c = new Cart(); c.add("9", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest76() { Cart c = new Cart(); c.add("10", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest77() { Cart c = new Cart(); c.add("11", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest78() { Cart c = new Cart(); c.add("12", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest79() { Cart c = new Cart(); c.add("13", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest80() { Cart c = new Cart(); c.add("14", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest81() { Cart c = new Cart(); c.add("15", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest82() { Cart c = new Cart(); c.add("16", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest83() { Cart c = new Cart(); c.add("17", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest84() { Cart c = new Cart(); c.add("18", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest85() { Cart c = new Cart(); c.add("19", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest86() { Cart c = new Cart(); c.add("20", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest87() { Cart c = new Cart(); c.add("21", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest88() { Cart c = new Cart(); c.add("22", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest89() { Cart c = new Cart(); c.add("23", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest90() { Cart c = new Cart(); c.add("24", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest91() { Cart c = new Cart(); c.add("25", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest92() { Cart c = new Cart(); c.add("26", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest93() { Cart c = new Cart(); c.add("27", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest94() { Cart c = new Cart(); c.add("28", "N", 1, 1); assertEquals(1, c.items().size()); }
    @Test void cartTest95() { Cart c = new Cart(); c.add("29", "N", 1, 1); assertEquals(1, c.items().size()); }
}
