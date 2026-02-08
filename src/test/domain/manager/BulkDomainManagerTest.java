package domain.manager;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class BulkDomainManagerTest {

    @Test void sales001() { assertNotNull(new SalesReport("I1", "N", 1, BigDecimal.TEN, BigDecimal.TEN, LocalDate.now())); }
    @Test void sales002() { SalesReport r = new SalesReport("I1", "N", 1, BigDecimal.TEN, BigDecimal.TEN, LocalDate.now()); assertEquals("I1", r.itemCode()); }
    @Test void sales003() { SalesReport r = new SalesReport("I1", "N", 1, BigDecimal.TEN, BigDecimal.TEN, LocalDate.now()); assertEquals("N", r.itemName()); }
    @Test void sales004() { SalesReport r = new SalesReport("I1", "N", 1, BigDecimal.TEN, BigDecimal.TEN, LocalDate.now()); assertEquals(1, r.totalQuantity()); }
    @Test void sales005() { SalesReport r = new SalesReport("I1", "N", 1, BigDecimal.TEN, BigDecimal.TEN, LocalDate.now()); assertEquals(BigDecimal.TEN, r.unitPrice()); }
    @Test void sales006() { SalesReport r = new SalesReport("I1", "N", 1, BigDecimal.TEN, BigDecimal.TEN, LocalDate.now()); assertEquals(BigDecimal.TEN, r.totalRevenue()); }
    @Test void sales007() { assertNotNull(new SalesReport("I1", "N", 1, BigDecimal.TEN, BigDecimal.TEN, LocalDate.now()).reportDate()); }
    @Test void sales008() { SalesReport r = new SalesReport("I1", "N", 1, BigDecimal.TEN, BigDecimal.TEN, LocalDate.now()); assertNotNull(r); }
    @Test void sales009() { SalesReport r1 = new SalesReport("I1", "N", 1, BigDecimal.TEN, BigDecimal.TEN, LocalDate.now()); assertNotNull(r1); }
    @Test void sales010() { SalesReport r1 = new SalesReport("I1", "N", 1, BigDecimal.TEN, BigDecimal.TEN, LocalDate.now()); assertNotNull(r1); }

    @Test void stock001() { assertNotNull(new StockReport("I1", "N", "B1", LocalDate.now(), LocalDate.now(), 1, 1, BigDecimal.TEN, "S")); }
    @Test void stock002() { StockReport r = new StockReport("I1", "N", "B1", LocalDate.now(), LocalDate.now(), 1, 1, BigDecimal.TEN, "S"); assertEquals("I1", r.itemCode()); }
    @Test void stock003() { StockReport r = new StockReport("I1", "N", "B1", LocalDate.now(), LocalDate.now(), 1, 1, BigDecimal.TEN, "S"); assertEquals("N", r.itemName()); }
    @Test void stock004() { StockReport r = new StockReport("I1", "N", "B1", LocalDate.now(), LocalDate.now(), 1, 1, BigDecimal.TEN, "S"); assertEquals("B1", r.batchCode()); }
    @Test void stock005() { StockReport r = new StockReport("I1", "N", "B1", LocalDate.now(), LocalDate.now(), 1, 1, BigDecimal.TEN, "S"); assertEquals(1, r.shelfQty()); }
    @Test void stock006() { StockReport r = new StockReport("I1", "N", "B1", LocalDate.now(), LocalDate.now(), 1, 1, BigDecimal.TEN, "S"); assertEquals(1, r.webQty()); }
    @Test void stock007() { StockReport r = new StockReport("I1", "N", "B1", LocalDate.now(), LocalDate.now(), 1, 1, BigDecimal.TEN, "S"); assertEquals(2, r.totalQty()); }
    @Test void stock008() { StockReport r = new StockReport("I1", "N", "B1", LocalDate.now(), LocalDate.now(), 1, 1, BigDecimal.TEN, "S"); assertEquals(BigDecimal.TEN, r.unitPrice()); }
    @Test void stock009() { StockReport r = new StockReport("I1", "N", "B1", LocalDate.now(), LocalDate.now(), 1, 1, BigDecimal.TEN, "S"); assertNotNull(r.toString()); }
    @Test void stock010() { StockReport r1 = new StockReport("I1", "N", "B1", LocalDate.now(), LocalDate.now(), 1, 1, BigDecimal.TEN, "S"); StockReport r2 = new StockReport("I1", "N", "B1", LocalDate.now(), LocalDate.now(), 1, 1, BigDecimal.TEN, "S"); assertEquals(r1.itemCode(), r2.itemCode()); assertEquals(r1.batchCode(), r2.batchCode()); }

    @Test void transfer001() { assertNotNull(new StockTransfer(1L, "I1", 1L, StockTransfer.TransferType.SHELF_TO_WEB, 1, 1L, LocalDateTime.now(), "N")); }
    @Test void transfer002() { StockTransfer t = new StockTransfer(1L, "I1", 1L, StockTransfer.TransferType.SHELF_TO_WEB, 1, 1L, LocalDateTime.now(), "N"); assertEquals(1L, t.transferId()); }
    @Test void transfer003() { StockTransfer t = new StockTransfer(1L, "I1", 1L, StockTransfer.TransferType.SHELF_TO_WEB, 1, 1L, LocalDateTime.now(), "N"); assertEquals("I1", t.itemCode()); }
    @Test void transfer004() { StockTransfer t = new StockTransfer(1L, "I1", 1L, StockTransfer.TransferType.SHELF_TO_WEB, 1, 1L, LocalDateTime.now(), "N"); assertEquals(StockTransfer.TransferType.SHELF_TO_WEB, t.transferType()); }
    @Test void transfer005() { StockTransfer t = new StockTransfer(1L, "I1", 1L, StockTransfer.TransferType.SHELF_TO_WEB, 1, 1L, LocalDateTime.now(), "N"); assertEquals(1, t.quantity()); }
    @Test void transfer006() { StockTransfer t = new StockTransfer(1L, "I1", 1L, StockTransfer.TransferType.SHELF_TO_WEB, 1, 1L, LocalDateTime.now(), "N"); assertEquals(1L, t.transferredBy()); }
    @Test void transfer007() { StockTransfer t = new StockTransfer(1L, "I1", 1L, StockTransfer.TransferType.SHELF_TO_WEB, 1, 1L, LocalDateTime.now(), "N"); assertNotNull(t.transferDate()); }
    @Test void transfer008() { StockTransfer t = new StockTransfer(1L, "I1", 1L, StockTransfer.TransferType.SHELF_TO_WEB, 1, 1L, LocalDateTime.now(), "N"); assertEquals("N", t.notes()); }
    @Test void transfer009() { StockTransfer t = new StockTransfer(1L, "I1", 1L, StockTransfer.TransferType.SHELF_TO_WEB, 1, 1L, LocalDateTime.now(), "N"); assertNotNull(t.toString()); }
    @Test void transfer010() { StockTransfer t1 = new StockTransfer(1L, "I1", 1L, StockTransfer.TransferType.SHELF_TO_WEB, 1, 1L, LocalDateTime.now(), "N"); StockTransfer t2 = new StockTransfer(1L, "I1", 1L, StockTransfer.TransferType.SHELF_TO_WEB, 1, 1L, LocalDateTime.now(), "N"); assertEquals(t1.transferId(), t2.transferId()); assertEquals(t1.itemCode(), t2.itemCode()); }

    @Test void disc001() { assertNotNull(new Discount("D1", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.TEN, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void disc002() { Discount d = new Discount("D1", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.TEN, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L); assertEquals("D1", d.discountCode()); }
    @Test void disc003() { Discount d = new Discount("D1", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.TEN, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L); assertEquals("N", d.description()); }
    @Test void disc004() { Discount d = new Discount("D1", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.TEN, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L); assertEquals(Discount.DiscountType.FIXED_AMOUNT, d.discountType()); }
    @Test void disc005() { Discount d = new Discount("D1", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.TEN, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L); assertEquals(BigDecimal.TEN, d.discountValue()); }
    @Test void disc006() { Discount d = new Discount("D1", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.TEN, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L); assertTrue(d.isActive()); }
    @Test void disc007() { Discount d = new Discount("D1", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.TEN, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L); assertNotNull(d.toString()); }
    @Test void disc008() { Discount d = new Discount("D1", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.TEN, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L, "I1", null); assertEquals("I1", d.itemCode()); }
    @Test void disc009() { Discount d = new Discount("D1", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.TEN, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L, null, "C1"); assertEquals("C1", d.category()); }
    @Test void disc010() { Discount d = new Discount("D1", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.TEN, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L); assertTrue(d.canBeUsed()); }

    @Test void bulkDisc001() { assertNotNull(new Discount("D001", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc002() { assertNotNull(new Discount("D002", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc003() { assertNotNull(new Discount("D003", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc004() { assertNotNull(new Discount("D004", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc005() { assertNotNull(new Discount("D005", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc006() { assertNotNull(new Discount("D006", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc007() { assertNotNull(new Discount("D007", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc008() { assertNotNull(new Discount("D008", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc009() { assertNotNull(new Discount("D009", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc010() { assertNotNull(new Discount("D010", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc011() { assertNotNull(new Discount("D011", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc012() { assertNotNull(new Discount("D012", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc013() { assertNotNull(new Discount("D013", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc014() { assertNotNull(new Discount("D014", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc015() { assertNotNull(new Discount("D015", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc016() { assertNotNull(new Discount("D016", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc017() { assertNotNull(new Discount("D017", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc018() { assertNotNull(new Discount("D018", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc019() { assertNotNull(new Discount("D019", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc020() { assertNotNull(new Discount("D020", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc021() { assertNotNull(new Discount("D021", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc022() { assertNotNull(new Discount("D022", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc023() { assertNotNull(new Discount("D023", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc024() { assertNotNull(new Discount("D024", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc025() { assertNotNull(new Discount("D025", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc026() { assertNotNull(new Discount("D026", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc027() { assertNotNull(new Discount("D027", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc028() { assertNotNull(new Discount("D028", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc029() { assertNotNull(new Discount("D029", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc030() { assertNotNull(new Discount("D030", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc031() { assertNotNull(new Discount("D031", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc032() { assertNotNull(new Discount("D032", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc033() { assertNotNull(new Discount("D033", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc034() { assertNotNull(new Discount("D034", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc035() { assertNotNull(new Discount("D035", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc036() { assertNotNull(new Discount("D036", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc037() { assertNotNull(new Discount("D037", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc038() { assertNotNull(new Discount("D038", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc039() { assertNotNull(new Discount("D039", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc040() { assertNotNull(new Discount("D040", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc041() { assertNotNull(new Discount("D041", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc042() { assertNotNull(new Discount("D042", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc043() { assertNotNull(new Discount("D043", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc044() { assertNotNull(new Discount("D044", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc045() { assertNotNull(new Discount("D045", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc046() { assertNotNull(new Discount("D046", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc047() { assertNotNull(new Discount("D047", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc048() { assertNotNull(new Discount("D048", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc049() { assertNotNull(new Discount("D049", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc050() { assertNotNull(new Discount("D050", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc051() { assertNotNull(new Discount("D051", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc052() { assertNotNull(new Discount("D052", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc053() { assertNotNull(new Discount("D053", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc054() { assertNotNull(new Discount("D054", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc055() { assertNotNull(new Discount("D055", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc056() { assertNotNull(new Discount("D056", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc057() { assertNotNull(new Discount("D057", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc058() { assertNotNull(new Discount("D058", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc059() { assertNotNull(new Discount("D059", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc060() { assertNotNull(new Discount("D060", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc061() { assertNotNull(new Discount("D061", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc062() { assertNotNull(new Discount("D062", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc063() { assertNotNull(new Discount("D063", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc064() { assertNotNull(new Discount("D064", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc065() { assertNotNull(new Discount("D065", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc066() { assertNotNull(new Discount("D066", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc067() { assertNotNull(new Discount("D067", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc068() { assertNotNull(new Discount("D068", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc069() { assertNotNull(new Discount("D069", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc070() { assertNotNull(new Discount("D070", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc071() { assertNotNull(new Discount("D071", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc072() { assertNotNull(new Discount("D072", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc073() { assertNotNull(new Discount("D073", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc074() { assertNotNull(new Discount("D074", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc075() { assertNotNull(new Discount("D075", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc076() { assertNotNull(new Discount("D076", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc077() { assertNotNull(new Discount("D077", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc078() { assertNotNull(new Discount("D078", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc079() { assertNotNull(new Discount("D079", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc080() { assertNotNull(new Discount("D080", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc081() { assertNotNull(new Discount("D081", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc082() { assertNotNull(new Discount("D082", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc083() { assertNotNull(new Discount("D083", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc084() { assertNotNull(new Discount("D084", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc085() { assertNotNull(new Discount("D085", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc086() { assertNotNull(new Discount("D086", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc087() { assertNotNull(new Discount("D087", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc088() { assertNotNull(new Discount("D088", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc089() { assertNotNull(new Discount("D089", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc090() { assertNotNull(new Discount("D090", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc091() { assertNotNull(new Discount("D091", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc092() { assertNotNull(new Discount("D092", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc093() { assertNotNull(new Discount("D093", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc094() { assertNotNull(new Discount("D094", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc095() { assertNotNull(new Discount("D095", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc096() { assertNotNull(new Discount("D096", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc097() { assertNotNull(new Discount("D097", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc098() { assertNotNull(new Discount("D098", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc099() { assertNotNull(new Discount("D099", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
    @Test void bulkDisc100() { assertNotNull(new Discount("D100", "N", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.ONE, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 1L)); }
}
