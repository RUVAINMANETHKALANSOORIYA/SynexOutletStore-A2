package infrastructure.jdbc;

import domain.auth.User;
import ports.out.UserRepository;

import java.sql.*;
import java.util.Optional;

public final class JdbcUserRepository implements UserRepository {

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = """
                SELECT user_id, username, role, email, status
                FROM users
                WHERE username = ?
                """;

        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();

                return Optional.of(new User(
                        rs.getLong("user_id"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getString("email"),
                        rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException("findByUsername failed", e);
        }
    }

    @Override
    public String loadPasswordHash(String username) {
        String sql = "SELECT password_hash FROM users WHERE username = ?";

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
}
