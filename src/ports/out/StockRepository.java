package ports.out;

import java.sql.Connection;

public interface StockRepository {
    void deductWebStockFifo(Connection c, long orderId, String itemCode, int qtyNeeded);
    void deductShelfStockFifo(Connection c, long orderId, String itemCode, int qtyNeeded);

}
