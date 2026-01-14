package ports.in;

import domain.auth.PasswordHash;
import domain.customer.Customer;
import ports.out.CustomerRepository;

import java.util.Optional;

public final class CustomerAuthService {

    private final CustomerRepository customers;

    public CustomerAuthService(CustomerRepository customers) {
        this.customers = customers;
    }

    public Optional<Customer> login(String username, String password) {
        if (username == null || username.trim().isEmpty()) return Optional.empty();
        if (password == null) password = "";

        String u = username.trim();

        String stored = customers.loadPasswordHash(u);
        if (stored == null) return Optional.empty();

        boolean ok = PasswordHash.looksLikeSha256Hex(stored)
                ? PasswordHash.sha256(password).equalsIgnoreCase(stored)
                : password.equals(stored); // dev fallback

        if (!ok) return Optional.empty();

        Optional<Customer> c = customers.findByUsername(u);
        if (c.isEmpty()) return Optional.empty();

        if ("DISABLED".equalsIgnoreCase(c.get().status())) return Optional.empty();
        return c;
    }
}
