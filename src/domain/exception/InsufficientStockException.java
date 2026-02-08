package domain.exception;

/**
 * Exception thrown when there is insufficient stock to complete an operation.
 */
public class InsufficientStockException extends RuntimeException {
    private final String itemCode;
    private final int requestedQuantity;
    private final int availableQuantity;

    public InsufficientStockException(String itemCode, int requestedQuantity, int availableQuantity) {
        super(String.format("Insufficient stock for item %s: requested %d, available %d",
              itemCode, requestedQuantity, availableQuantity));
        this.itemCode = itemCode;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }

    public InsufficientStockException(String itemCode, int requestedQuantity) {
        super(String.format("Insufficient stock for item %s: requested %d", itemCode, requestedQuantity));
        this.itemCode = itemCode;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = 0;
    }

    public String getItemCode() {
        return itemCode;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }
}
