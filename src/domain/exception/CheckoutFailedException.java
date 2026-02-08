package domain.exception;

/**
 * Exception thrown when checkout process fails.
 */
public class CheckoutFailedException extends RuntimeException {

    public CheckoutFailedException(String message) {
        super(message);
    }

    public CheckoutFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public static CheckoutFailedException emptyCart() {
        return new CheckoutFailedException("Cannot checkout with empty cart");
    }

    public static CheckoutFailedException invalidPaymentMethod(String paymentMethod) {
        return new CheckoutFailedException("Invalid payment method: " + paymentMethod);
    }

    public static CheckoutFailedException stockDepletion(String itemCode, int requested, int available) {
        return new CheckoutFailedException(String.format(
            "Stock depleted during checkout for item %s: requested %d, available %d",
            itemCode, requested, available));
    }

    public static CheckoutFailedException paymentProcessingFailed(String reason) {
        return new CheckoutFailedException("Payment processing failed: " + reason);
    }

    public static CheckoutFailedException timeout() {
        return new CheckoutFailedException("Checkout process timed out");
    }

    public static CheckoutFailedException concurrencyError() {
        return new CheckoutFailedException("Checkout failed due to concurrent modification");
    }

    public static CheckoutFailedException concurrencyError(String message) {
        return new CheckoutFailedException("Concurrency error: " + message);
    }

    public static CheckoutFailedException invalidCustomer(String message) {
        return new CheckoutFailedException("Invalid customer: " + message);
    }

    public static CheckoutFailedException cartValidationFailed(String message) {
        return new CheckoutFailedException("Cart validation failed: " + message);
    }

    public static CheckoutFailedException invalidItem(String message) {
        return new CheckoutFailedException("Invalid item: " + message);
    }

    public static CheckoutFailedException invalidQuantity(String itemCode, int quantity) {
        return new CheckoutFailedException(String.format(
            "Invalid quantity %d for item %s", quantity, itemCode));
    }

    public static CheckoutFailedException invalidPrice(String itemCode, double price) {
        return new CheckoutFailedException(String.format(
            "Invalid price %.2f for item %s", price, itemCode));
    }

    public static CheckoutFailedException orderTooLarge(java.math.BigDecimal amount) {
        return new CheckoutFailedException("Order amount too large: " + amount);
    }

    public static CheckoutFailedException stockCheckFailed(String message) {
        return new CheckoutFailedException("Stock check failed: " + message);
    }

    public static CheckoutFailedException databaseConnectionFailed() {
        return new CheckoutFailedException("Database connection failed");
    }

    public static CheckoutFailedException orderCreationFailed(String message) {
        return new CheckoutFailedException("Order creation failed: " + message);
    }

    public static CheckoutFailedException dataConstraintViolation(String message) {
        return new CheckoutFailedException("Data constraint violation: " + message);
    }

    public static CheckoutFailedException databaseError(String message) {
        return new CheckoutFailedException("Database error: " + message);
    }

    public static CheckoutFailedException itemProcessingFailed(String message) {
        return new CheckoutFailedException("Item processing failed: " + message);
    }

    public static CheckoutFailedException unexpectedError(String message) {
        return new CheckoutFailedException("Unexpected error: " + message);
    }
}
