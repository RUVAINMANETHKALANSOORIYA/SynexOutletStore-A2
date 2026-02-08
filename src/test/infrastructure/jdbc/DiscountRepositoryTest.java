package infrastructure.jdbc;

import domain.manager.Discount;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class DiscountRepositoryTest {
    private JdbcDiscountRepository repository;

    @BeforeAll
    static void init() throws Exception {
        Class.forName("fakes.JdbcFakes$FakeDriver");
    }

    @BeforeEach
    void setUp() {
        repository = new JdbcDiscountRepository();
    }

    @Test
    void testCreateDiscount() {
        Discount d = new Discount(null, "DESC", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.TEN, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), 100, 1L);
        Discount created = repository.createDiscount(d);
        assertNotNull(created);
    }

    @Test
    void testFindById() {
        Optional<Discount> d = repository.findById(1);
        assertTrue(d.isPresent());
    }

    @Test
    void testFindByCode() {
        Optional<Discount> d = repository.findByCode("CODE");
        assertTrue(d.isPresent());
    }

    @Test
    void testFindAll() {
        assertFalse(repository.findAll().isEmpty());
    }

    @Test
    void testFindActiveDiscountsFIFO() {
        assertFalse(repository.findActiveDiscountsFIFO().isEmpty());
    }

    @Test
    void testFindValidDiscountsForAmount() {
        assertFalse(repository.findValidDiscountsForAmount(BigDecimal.valueOf(1000)).isEmpty());
    }

    @Test
    void testUpdateDiscount() {
        Discount d = new Discount(1, "CODE", "DESC", Discount.DiscountType.FIXED_AMOUNT, BigDecimal.TEN, BigDecimal.ZERO, null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), true, 100, 0, 1L, null, null, null, null);
        repository.updateDiscount(d);
    }

    @Test
    void testDeleteDiscount() {
        repository.deleteDiscount(1);
    }

    @Test
    void testIncrementUsageCount() {
        repository.incrementUsageCount(1);
    }

    @Test
    void testRecordDiscountUsage() {
        repository.recordDiscountUsage(1, 1, BigDecimal.TEN);
    }

    @Test
    void testFindDiscountsByCreator() {
        assertFalse(repository.findDiscountsByCreator(1L).isEmpty());
    }

    @Test
    void testGetTotalActiveDiscounts() {
        assertTrue(repository.getTotalActiveDiscounts() >= 0);
    }

    @Test
    void testGetTotalDiscountsSaved() {
        assertNotNull(repository.getTotalDiscountsSaved());
    }
}
