package domain.manager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class StockReport {
    private final String itemCode;
    private final String itemName;
    private final String batchCode;
    private final LocalDate purchaseDate;
    private final LocalDate expiryDate;
    private final int shelfQty;
    private final int webQty;
    private final int totalQty;
    private final BigDecimal unitPrice;
    private final String supplier;

    public StockReport(String itemCode, String itemName, String batchCode,
                      LocalDate purchaseDate, LocalDate expiryDate,
                      int shelfQty, int webQty, BigDecimal unitPrice, String supplier) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.batchCode = batchCode;
        this.purchaseDate = purchaseDate;
        this.expiryDate = expiryDate;
        this.shelfQty = shelfQty;
        this.webQty = webQty;
        this.totalQty = shelfQty + webQty;
        this.unitPrice = unitPrice;
        this.supplier = supplier;
    }

    // Getters
    public String itemCode() { return itemCode; }
    public String itemName() { return itemName; }
    public String batchCode() { return batchCode; }
    public LocalDate purchaseDate() { return purchaseDate; }
    public LocalDate expiryDate() { return expiryDate; }
    public int shelfQty() { return shelfQty; }
    public int webQty() { return webQty; }
    public int totalQty() { return totalQty; }
    public BigDecimal unitPrice() { return unitPrice; }
    public String supplier() { return supplier; }
}
