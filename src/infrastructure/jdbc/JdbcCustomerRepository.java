package infrastructure.jdbc;

import domain.customer.Customer;
import ports.out.CustomerRepository;

import java.sql.*;
import java.util.Optional;

public final class JdbcCustomerRepository implements CustomerRepository {

    @Override
    public Optional<Customer> findByUsername(String username) {
        String sql = """
                SELECT customer_id, username, full_name, email, phone, status
                FROM customers
                WHERE username = ?
                """;

        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();

                return Optional.of(new Customer(
                        rs.getLong("customer_id"),
                        rs.getString("username"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("findByUsername failed", e);
        }
    }

    @Override
    public String loadPasswordHash(String username) {
        String sql = "SELECT password_hash FROM customers WHERE username = ?";

        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("password_hash") : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("loadPasswordHash failed", e);
        }
    }

    @Override
    public long insertCustomer(String username, String passwordHash, String fullName, String email, String phone) {
        String sql = """
                INSERT INTO customers (username, password_hash, full_name, email, phone, status)
                VALUES (?, ?, ?, ?, ?, 'ACTIVE')
                """;

        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setString(3, fullName);
            ps.setString(4, email);
            ps.setString(5, phone);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
            throw new SQLException("No generated key returned");

        } catch (SQLException e) {
            throw new RuntimeException("insertCustomer failed", e);
        }
    }
}
