package domain.manager;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class StockTransfer {
    private final Long transferId;
    private final String itemCode;
    private final Long stockId;
    private final TransferType transferType;
    private final int quantity;
    private final Long transferredBy;
    private final LocalDateTime transferDate;
    private final String notes;

    public StockTransfer(Long transferId, String itemCode, Long stockId,
                        TransferType transferType, int quantity, Long transferredBy,
                        LocalDateTime transferDate, String notes) {
        this.transferId = transferId;
        this.itemCode = itemCode;
        this.stockId = stockId;
        this.transferType = transferType;
        this.quantity = quantity;
        this.transferredBy = transferredBy;
        this.transferDate = transferDate;
        this.notes = notes;
    }

    public enum TransferType {
        SHELF_TO_WEB, WEB_TO_SHELF
    }

    // Getters
    public Long transferId() { return transferId; }
    public String itemCode() { return itemCode; }
    public Long stockId() { return stockId; }
    public TransferType transferType() { return transferType; }
    public int quantity() { return quantity; }
    public Long transferredBy() { return transferredBy; }
    public LocalDateTime transferDate() { return transferDate; }
    public String notes() { return notes; }
}
