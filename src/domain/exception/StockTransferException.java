package domain.exception;

/**
 * Exception thrown when stock transfer operations fail.
 */
public class StockTransferException extends RuntimeException {

    public StockTransferException(String message) {
        super(message);
    }

    public StockTransferException(String message, Throwable cause) {
        super(message, cause);
    }

    public static StockTransferException insufficientStock(String itemCode, int requested, int available) {
        return new StockTransferException(String.format(
            "Insufficient shelf stock for transfer of item %s: requested %d, available %d",
            itemCode, requested, available));
    }

    public static StockTransferException invalidQuantity(int quantity) {
        return new StockTransferException("Invalid transfer quantity: " + quantity + ". Must be positive.");
    }

    public static StockTransferException invalidItemCode(String itemCode) {
        return new StockTransferException("Invalid item code: " + (itemCode == null ? "null" : "'" + itemCode + "'"));
    }

    public static StockTransferException itemNotFound(String itemCode) {
        return new StockTransferException("Item not found: " + itemCode);
    }

    public static StockTransferException inactiveItem(String itemCode) {
        return new StockTransferException("Cannot transfer stock for inactive item: " + itemCode);
    }

    public static StockTransferException sameLocation() {
        return new StockTransferException("Cannot transfer stock to the same location");
    }

    public static StockTransferException concurrentModification(String itemCode) {
        return new StockTransferException("Stock was modified by another transaction for item: " + itemCode);
    }
}
