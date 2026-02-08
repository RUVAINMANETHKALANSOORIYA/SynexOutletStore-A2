package domain.exception;

/**
 * Exception thrown when an invalid cart operation is attempted.
 */
public class InvalidCartOperationException extends RuntimeException {

    public InvalidCartOperationException(String message) {
        super(message);
    }

    public InvalidCartOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public static InvalidCartOperationException emptyCart() {
        return new InvalidCartOperationException("Cannot perform operation on empty cart");
    }

    public static InvalidCartOperationException invalidQuantity(int quantity) {
        return new InvalidCartOperationException("Invalid quantity: " + quantity + ". Quantity must be positive.");
    }

    public static InvalidCartOperationException invalidItemCode(String itemCode) {
        return new InvalidCartOperationException("Invalid item code: " + (itemCode == null ? "null" : "'" + itemCode + "'"));
    }

    public static InvalidCartOperationException cartSizeLimit(int maxSize) {
        return new InvalidCartOperationException("Cart size limit exceeded. Maximum allowed: " + maxSize + " unique items");
    }

    public static InvalidCartOperationException itemNotFound(String itemCode) {
        return new InvalidCartOperationException("Item not found in cart: " + itemCode);
    }

    public static InvalidCartOperationException itemNotExist(String itemCode) {
        return new InvalidCartOperationException("Item does not exist: " + itemCode);
    }
}
