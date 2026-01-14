package domain.customer;

public record Customer(
        long id,
        String username,
        String fullName,
        String email,
        String phone,
        String status
) {}
