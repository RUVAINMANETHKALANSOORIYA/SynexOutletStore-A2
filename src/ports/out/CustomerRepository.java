package ports.out;

import domain.customer.Customer;

import java.util.Optional;

public interface CustomerRepository {
    Optional<Customer> findByUsername(String username);
    String loadPasswordHash(String username);
    long insertCustomer(String username, String passwordHash, String fullName, String email, String phone);
}
