package domain.manager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class ManagerDomainTest {

    @Test
    @DisplayName("Should create SalesReport and retrieve properties")
    void testSalesReport() {
        LocalDate date = LocalDate.now();
        SalesReport report = new SalesReport("ITM001", "Milk", 10, new BigDecimal("450.00"), new BigDecimal("4500.00"), date);
        
        assertEquals("ITM001", report.itemCode());
        assertEquals("Milk", report.itemName());
        assertEquals(10, report.totalQuantity());
        assertEquals(new BigDecimal("450.00"), report.unitPrice());
        assertEquals(new BigDecimal("4500.00"), report.totalRevenue());
        assertEquals(date, report.reportDate());
    }

    @Test
    @DisplayName("Should create StockReport and retrieve properties")
    void testStockReport() {
        LocalDate now = LocalDate.now();
        StockReport report = new StockReport("ITM001", "Milk", "BATCH01", now, now.plusDays(10), 50, 30, new BigDecimal("450.00"), "Supplier A");
        
        assertEquals("ITM001", report.itemCode());
        assertEquals("Milk", report.itemName());
        assertEquals("BATCH01", report.batchCode());
        assertEquals(now, report.purchaseDate());
        assertEquals(now.plusDays(10), report.expiryDate());
        assertEquals(50, report.shelfQty());
        assertEquals(30, report.webQty());
        assertEquals(80, report.totalQty());
        assertEquals(new BigDecimal("450.00"), report.unitPrice());
        assertEquals("Supplier A", report.supplier());
    }

    @Test
    @DisplayName("Should create OrderReport and retrieve properties")
    void testOrderReport() {
        LocalDateTime now = LocalDateTime.now();
        OrderReport report = new OrderReport(1L, "ONLINE", "CARD", new BigDecimal("1250.00"), "John Doe", now);
        
        assertEquals(1L, report.orderId());
        assertEquals("ONLINE", report.orderType());
        assertEquals("CARD", report.paymentMethod());
        assertEquals(new BigDecimal("1250.00"), report.totalAmount());
        assertEquals("John Doe", report.customerInfo());
        assertEquals(now, report.createdAt());
    }

    @Test
    @DisplayName("Should create StockTransfer and retrieve properties")
    void testStockTransfer() {
        LocalDateTime now = LocalDateTime.now();
        StockTransfer transfer = new StockTransfer(1L, "ITM001", 100L, StockTransfer.TransferType.SHELF_TO_WEB, 5, 2L, now, "Moving to web");
        
        assertEquals(1L, transfer.transferId());
        assertEquals("ITM001", transfer.itemCode());
        assertEquals(100L, transfer.stockId());
        assertEquals(StockTransfer.TransferType.SHELF_TO_WEB, transfer.transferType());
        assertEquals(5, transfer.quantity());
        assertEquals(2L, transfer.transferredBy());
        assertEquals(now, transfer.transferDate());
        assertEquals("Moving to web", transfer.notes());
    }
}
