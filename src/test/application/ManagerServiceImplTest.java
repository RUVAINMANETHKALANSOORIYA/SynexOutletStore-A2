package application;

import domain.manager.StockTransfer;
import domain.store.Item;
import fakes.FakeManagerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ManagerServiceImplTest {
    private ManagerServiceImpl managerService;
    private FakeManagerRepository managerRepo;

    @BeforeEach
    void setUp() {
        managerRepo = new FakeManagerRepository();
        managerService = new ManagerServiceImpl(managerRepo);
    }

    @Test
    @DisplayName("Should create item and log activity")
    void testCreateItem() {
        managerService.createItem("ITM001", "Milk", 450.0, 1L);
        
        var item = managerRepo.getItemByCode("ITM001");
        assertTrue(item.isPresent());
        assertEquals("Milk", item.get().name());
        assertEquals(1, managerRepo.getActivities().size());
        assertTrue(managerRepo.getActivities().get(0).contains("ITEM_CREATED"));
    }

    @Test
    @DisplayName("Should fail to create item if code or name is empty")
    void testCreateItemInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> managerService.createItem("", "Milk", 450.0, 1L));
        assertThrows(IllegalArgumentException.class, () -> managerService.createItem("ITM001", "", 450.0, 1L));
        assertThrows(IllegalArgumentException.class, () -> managerService.createItem("ITM001", "Milk", -1.0, 1L));
    }

    @Test
    @DisplayName("Should update item and log activity")
    void testUpdateItem() {
        managerRepo.createItem("ITM001", "Milk", 450.0);
        managerService.updateItem("ITM001", "Fresh Milk", 480.0, true, 1L);
        
        var item = managerRepo.getItemByCode("ITM001").get();
        assertEquals("Fresh Milk", item.name());
        assertEquals(480.0, item.price().doubleValue());
    }

    @Test
    @DisplayName("Should deactivate item")
    void testDeactivateItem() {
        managerRepo.createItem("ITM001", "Milk", 450.0);
        managerService.deactivateItem("ITM001", 1L);
        
        var item = managerRepo.getItemByCode("ITM001").get();
        assertFalse(item.isActive());
    }

    @Test
    @DisplayName("Should transfer stock and log activity")
    void testTransferStock() {
        managerRepo.createItem("ITM001", "Milk", 450.0);
        managerService.transferStockShelfToWeb("ITM001", 10, 1L, "Refill web");
        
        assertEquals(1, managerRepo.getTransferHistory(LocalDate.now()).size());
        assertTrue(managerRepo.getActivities().stream().anyMatch(a -> a.contains("STOCK_TRANSFER")));
    }

    @Test
    @DisplayName("Should fail report generation for future date")
    void testGenerateReportFutureDate() {
        LocalDate future = LocalDate.now().plusDays(1);
        assertThrows(IllegalArgumentException.class, () -> managerService.generateDailySalesReport(future));
    }
}
