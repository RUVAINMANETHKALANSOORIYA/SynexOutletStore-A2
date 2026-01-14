package domain.manager;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SalesReport {
    private final String itemCode;
    private final String itemName;
    private final int totalQuantity;
    private final BigDecimal unitPrice;
    private final BigDecimal totalRevenue;
    private final LocalDate reportDate;

    public SalesReport(String itemCode, String itemName, int totalQuantity,
                      BigDecimal unitPrice, BigDecimal totalRevenue, LocalDate reportDate) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.totalQuantity = totalQuantity;
        this.unitPrice = unitPrice;
        this.totalRevenue = totalRevenue;
        this.reportDate = reportDate;
    }

    // Getters
    public String itemCode() { return itemCode; }
    public String itemName() { return itemName; }
    public int totalQuantity() { return totalQuantity; }
    public BigDecimal unitPrice() { return unitPrice; }
    public BigDecimal totalRevenue() { return totalRevenue; }
    public LocalDate reportDate() { return reportDate; }
}
