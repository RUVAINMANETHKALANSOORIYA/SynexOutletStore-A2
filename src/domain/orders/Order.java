package domain.orders;

public record Order(
        Long userId,        // can be null for online customer
        Long customerId,    // online customer id
        OrderType orderType,
        PaymentMethod paymentMethod,
        double totalAmount
) {}
