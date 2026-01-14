package infrastructure.jdbc;

import domain.auth.User;
import ports.out.UserAdminRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class JdbcUserAdminRepository implements UserAdminRepository {

    @Override
    public boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ? LIMIT 1";
        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("usernameExists failed", e);
        }
    }

    @Override
    public long insertCashier(String username, String passwordHash, String email) {
        String sql = """
                INSERT INTO users (username, password_hash, role, email, status)
                VALUES (?, ?, 'CASHIER', ?, 'ACTIVE')
                """;

        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setString(3, email);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
            throw new SQLException("No generated key returned");

        } catch (SQLException e) {
            throw new RuntimeException("insertCashier failed", e);
        }
    }

    @Override
    public List<User> listStaffUsers() {
        String sql = """
                SELECT user_id, username, role, email, status
                FROM users
                WHERE role IN ('MANAGER','CASHIER')
                ORDER BY role, username
                """;

        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<User> list = new ArrayList<>();

            while (rs.next()) {
                list.add(new User(
                        rs.getLong("user_id"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getString("email"),
                        rs.getString("status")
                ));
            }

            return list;

        } catch (SQLException e) {
            throw new RuntimeException("listStaffUsers failed", e);
        }
    }
}
