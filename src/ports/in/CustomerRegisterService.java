package ports.in;

import domain.auth.PasswordHash;
import ports.out.CustomerRepository;

public final class CustomerRegisterService {

    private final CustomerRepository customers;

    public CustomerRegisterService(CustomerRepository customers) {
        this.customers = customers;
    }

    public long register(String username, String password, String fullName, String email, String phone) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username required");
        }
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters");
        }

        String u = username.trim();

        if (customers.findByUsername(u).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        String hash = PasswordHash.sha256(password);
        return customers.insertCustomer(u, hash, fullName, email, phone);
    }
}
