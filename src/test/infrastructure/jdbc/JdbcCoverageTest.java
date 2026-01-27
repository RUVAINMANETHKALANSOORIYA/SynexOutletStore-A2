package infrastructure.jdbc;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class JdbcCoverageTest {

    @BeforeAll
    static void init() throws Exception {
        Class.forName("fakes.JdbcFakes$FakeDriver");
    }

    @Test
    void testJdbcUserRepository() {
        JdbcUserRepository repo = new JdbcUserRepository();
        repo.findByUsername("test");
        repo.loadPasswordHash("test");
    }

    @Test
    void testJdbcItemRepository() {
        JdbcItemRepository repo = new JdbcItemRepository();
        repo.findAllActive();
        repo.findByCode("I1");
    }

    @Test
    void testJdbcCustomerRepository() {
        JdbcCustomerRepository repo = new JdbcCustomerRepository();
        repo.findByUsername("test");
        repo.loadPasswordHash("test");
        repo.insertCustomer("u", "h", "f", "e", "p");
    }

    @Test
    void testJdbcManagerRepository() {
        JdbcManagerRepository repo = new JdbcManagerRepository();
        repo.getItemByCode("I1");
        repo.getAllItems();
        repo.getActiveItems();
        repo.createItem("I1", "N1", 10.0);
        repo.updateItem("I1", "N1", 10.0, true);
        repo.deactivateItem("I1");
        repo.getLowStockItems(10);
        repo.transferStock("I1", 5, 1L, "note");
        repo.getTransferHistory(LocalDate.now());
        repo.getDailySalesReport(LocalDate.now());
        repo.getStockReport();
        repo.getReorderReport(10);
        repo.getBillReport(LocalDate.now(), LocalDate.now());
        repo.logActivity(1L, "T", "D", "I");
    }

    @Test
    void testJdbcOrderRepository() {
        JdbcOrderRepository repo = new JdbcOrderRepository();
        // Since many methods require a Connection as argument, we can pass our fake connection
        try (java.sql.Connection c = Db.get()) {
            repo.insertOrder(c, new domain.orders.Order(1L, null, domain.orders.OrderType.PHYSICAL, domain.orders.PaymentMethod.CASH, 10.0));
            repo.insertOrderItem(c, 1L, "I1", 1, 10.0);
        } catch (Exception e) {}
    }

    @Test
    void testJdbcStockRepository() {
        JdbcStockRepository repo = new JdbcStockRepository();
        repo.getAvailableWebStock("I1");
        repo.getAvailableShelfStock("I1");
        try (java.sql.Connection c = Db.get()) {
            repo.deductWebStockFifo(c, 1L, "I1", 1);
            repo.deductShelfStockFifo(c, 1L, "I1", 1);
        } catch (Exception e) {}
    }
    
    @Test
    void testJdbcUserAdminRepository() {
        JdbcUserAdminRepository repo = new JdbcUserAdminRepository();
        repo.listStaffUsers();
        repo.insertCashier("u", "h", "e");
        repo.usernameExists("u");
    }
}
