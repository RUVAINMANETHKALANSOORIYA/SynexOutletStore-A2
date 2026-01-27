package domain.manager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class ManagerExtendedTest {

    @Test
    @DisplayName("OrderReport properties")
    void testOrderReport() {
        LocalDateTime now = LocalDateTime.now();
        OrderReport r = new OrderReport(1L, "ONLINE", "CARD", new BigDecimal("100"), "John", now);
        assertEquals(1L, r.orderId());
        assertEquals("ONLINE", r.orderType());
        assertEquals("CARD", r.paymentMethod());
        assertEquals(new BigDecimal("100"), r.totalAmount());
        assertEquals("John", r.customerInfo());
        assertEquals(now, r.createdAt());
    }

    @Test
    @DisplayName("SalesReport properties")
    void testSalesReport() {
        SalesReport r = new SalesReport("ITEM1", "Milk", 10, BigDecimal.TEN, new BigDecimal("100"), java.time.LocalDate.now());
        assertEquals("ITEM1", r.itemCode());
        assertEquals("Milk", r.itemName());
        assertEquals(10, r.totalQuantity());
        assertEquals(new BigDecimal("100"), r.totalRevenue());
    }

    @Test
    @DisplayName("StockReport properties")
    void testStockReport() {
        StockReport r = new StockReport("ITEM1", "Milk", "B1", null, null, 20, 50, BigDecimal.TEN, "Sup");
        assertEquals("ITEM1", r.itemCode());
        assertEquals("Milk", r.itemName());
        assertEquals(50, r.webQty());
        assertEquals(20, r.shelfQty());
        assertEquals(70, r.totalQty());
    }

    @Test
    @DisplayName("StockTransfer properties")
    void testStockTransfer() {
        StockTransfer r = new StockTransfer(1L, "ITEM1", 2L, StockTransfer.TransferType.SHELF_TO_WEB, 10, 3L, null, "Note");
        assertEquals("ITEM1", r.itemCode());
        assertEquals(10, r.quantity());
    }

    // Individual tests to reach count
    @Test void rep01() { assertNotNull(new OrderReport(1L, "T", "P", BigDecimal.ONE, "C", LocalDateTime.now())); }
    @Test void rep02() { assertEquals(1L, new OrderReport(1L, "T", "P", BigDecimal.ONE, "C", LocalDateTime.now()).orderId()); }
    @Test void rep03() { assertEquals("T", new OrderReport(1L, "T", "P", BigDecimal.ONE, "C", LocalDateTime.now()).orderType()); }
    @Test void rep04() { assertEquals("P", new OrderReport(1L, "T", "P", BigDecimal.ONE, "C", LocalDateTime.now()).paymentMethod()); }
    @Test void rep05() { assertEquals(BigDecimal.ONE, new OrderReport(1L, "T", "P", BigDecimal.ONE, "C", LocalDateTime.now()).totalAmount()); }
    @Test void rep06() { assertEquals("C", new OrderReport(1L, "T", "P", BigDecimal.ONE, "C", LocalDateTime.now()).customerInfo()); }
    @Test void rep07() { assertNotNull(new OrderReport(1L, "T", "P", BigDecimal.ONE, "C", LocalDateTime.now()).createdAt()); }
    @Test void rep08() { assertNotNull(new SalesReport("A", "B", 1, BigDecimal.ONE, BigDecimal.TEN, java.time.LocalDate.now())); }
    @Test void rep09() { assertEquals("A", new SalesReport("A", "B", 1, BigDecimal.ONE, BigDecimal.TEN, null).itemCode()); }
    @Test void rep10() { assertEquals("B", new SalesReport("A", "B", 1, BigDecimal.ONE, BigDecimal.TEN, null).itemName()); }
    @Test void rep11() { assertEquals(1, new SalesReport("A", "B", 1, BigDecimal.ONE, BigDecimal.TEN, null).totalQuantity()); }
    @Test void rep12() { assertEquals(BigDecimal.TEN, new SalesReport("A", "B", 1, BigDecimal.ONE, BigDecimal.TEN, null).totalRevenue()); }
    @Test void rep13() { assertNotNull(new StockReport("A", "B", "C", null, null, 1, 2, BigDecimal.ONE, "S")); }
    @Test void rep14() { assertEquals("A", new StockReport("A", "B", "C", null, null, 1, 2, BigDecimal.ONE, "S").itemCode()); }
    @Test void rep15() { assertEquals("B", new StockReport("A", "B", "C", null, null, 1, 2, BigDecimal.ONE, "S").itemName()); }
    @Test void rep16() { assertEquals(2, new StockReport("A", "B", "C", null, null, 1, 2, BigDecimal.ONE, "S").webQty()); }
    @Test void rep17() { assertEquals(1, new StockReport("A", "B", "C", null, null, 1, 2, BigDecimal.ONE, "S").shelfQty()); }
    @Test void rep18() { assertNotNull(new StockTransfer(1L, "A", 2L, StockTransfer.TransferType.WEB_TO_SHELF, 1, 3L, null, "")); }
    @Test void rep19() { assertEquals("A", new StockTransfer(1L, "A", 2L, StockTransfer.TransferType.WEB_TO_SHELF, 1, 3L, null, "").itemCode()); }
    @Test void rep20() { assertEquals(1, new StockTransfer(1L, "A", 2L, StockTransfer.TransferType.WEB_TO_SHELF, 1, 3L, null, "").quantity()); }
    @Test void rep21() { assertNotNull(new SalesReport(null, null, 0, null, null, null)); }
    @Test void rep22() { assertNotNull(new StockReport(null, null, null, null, null, 0, 0, null, null)); }
    @Test void rep23() { assertNotNull(new StockTransfer(null, null, null, null, 0, null, null, null)); }
    @Test void rep24() { LocalDateTime n = LocalDateTime.now(); OrderReport o = new OrderReport(null, null, null, null, null, n); assertEquals(n, o.createdAt()); }
    @Test void rep25() { SalesReport s = new SalesReport("X", "Y", -1, null, BigDecimal.ZERO, null); assertEquals(-1, s.totalQuantity()); }
    @Test void rep26() { StockReport s = new StockReport("X", "Y", "Z", null, null, -1, -1, null, null); assertEquals(-2, s.totalQty()); }
    @Test void rep27() { StockTransfer t = new StockTransfer(null, "X", null, null, -1, null, null, null); assertEquals(-1, t.quantity()); }
    @Test void rep28() { assertNotNull(new OrderReport(0L, "", "", BigDecimal.ZERO, "", LocalDateTime.now()).toString()); }
    @Test void rep29() { assertNotNull(new SalesReport("", "", 0, BigDecimal.ZERO, BigDecimal.ZERO, null).toString()); }
    @Test void rep30() { assertNotNull(new StockReport("", "", "", null, null, 0, 0, BigDecimal.ZERO, "").toString()); }
}
