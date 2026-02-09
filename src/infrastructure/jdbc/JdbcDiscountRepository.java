package infrastructure.jdbc;

import domain.manager.Discount;
import domain.manager.Discount.DiscountType;
import ports.out.DiscountRepository;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcDiscountRepository implements DiscountRepository {

    @Override
    public Discount createDiscount(Discount discount) {
        String sql = """
            INSERT INTO discounts (discount_code, description, discount_type, discount_value,
                                 min_purchase_amount, max_discount_amount, start_date, end_date,
                                 is_active, usage_limit, created_by, item_code, category)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = Db.get();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, discount.discountCode());
            stmt.setString(2, discount.description());
            stmt.setString(3, discount.discountType().name());
            stmt.setBigDecimal(4, discount.discountValue());
            stmt.setBigDecimal(5, discount.minPurchaseAmount());

            if (discount.maxDiscountAmount() != null) {
                stmt.setBigDecimal(6, discount.maxDiscountAmount());
            } else {
                stmt.setNull(6, Types.DECIMAL);
            }

            stmt.setTimestamp(7, Timestamp.valueOf(discount.startDate()));
            stmt.setTimestamp(8, Timestamp.valueOf(discount.endDate()));
            stmt.setBoolean(9, discount.isActive());

            if (discount.usageLimit() != null) {
                stmt.setInt(10, discount.usageLimit());
            } else {
                stmt.setNull(10, Types.INTEGER);
            }

            stmt.setLong(11, discount.createdBy());

            if (discount.itemCode() != null) {
                stmt.setString(12, discount.itemCode());
            } else {
                stmt.setNull(12, Types.VARCHAR);
            }

            if (discount.category() != null) {
                stmt.setString(13, discount.category());
            } else {
                stmt.setNull(13, Types.VARCHAR);
            }

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Creating discount failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int discountId = generatedKeys.getInt(1);
                    return new Discount(
                        discountId, // generated discount_id
                        discount.discountCode(),
                        discount.description(),
                        discount.discountType(),
                        discount.discountValue(),
                        discount.minPurchaseAmount(),
                        discount.maxDiscountAmount(),
                        discount.startDate(),
                        discount.endDate(),
                        discount.isActive(),
                        discount.usageLimit(),
                        0, // times_used starts at 0
                        discount.createdBy(),
                        LocalDateTime.now(), // created_at
                        LocalDateTime.now(), // updated_at
                        discount.itemCode(),
                        discount.category()
                    );
                } else {
                    throw new RuntimeException("Creating discount failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to create discount: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Discount> findById(int discountId) {
        String sql = "SELECT * FROM discounts WHERE discount_id = ?";

        try (Connection conn = Db.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, discountId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDiscount(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find discount by ID: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Discount> findByCode(String discountCode) {
        String sql = "SELECT * FROM discounts WHERE discount_code = ?";

        try (Connection conn = Db.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, discountCode);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDiscount(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find discount by code: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    @Override
    public List<Discount> findAll() {
        String sql = "SELECT * FROM discounts ORDER BY created_at DESC";
        List<Discount> discounts = new ArrayList<>();

        try (Connection conn = Db.get();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                discounts.add(mapResultSetToDiscount(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch all discounts: " + e.getMessage(), e);
        }

        return discounts;
    }

    @Override
    public List<Discount> findActiveDiscountsFIFO() {
        String sql = """
            SELECT * FROM discounts
            WHERE is_active = TRUE 
              AND start_date <= NOW() 
              AND end_date >= NOW()
              AND (usage_limit IS NULL OR times_used < usage_limit)
            ORDER BY start_date ASC
            """;

        List<Discount> discounts = new ArrayList<>();

        try (Connection conn = Db.get();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                discounts.add(mapResultSetToDiscount(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch active discounts FIFO: " + e.getMessage(), e);
        }

        return discounts;
    }

    @Override
    public List<Discount> findValidDiscountsForAmount(BigDecimal purchaseAmount) {
        String sql = """
            SELECT * FROM discounts
            WHERE is_active = TRUE 
              AND start_date <= NOW() 
              AND end_date >= NOW()
              AND min_purchase_amount <= ?
              AND (usage_limit IS NULL OR times_used < usage_limit)
            ORDER BY start_date ASC
            """;

        List<Discount> discounts = new ArrayList<>();

        try (Connection conn = Db.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, purchaseAmount);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    discounts.add(mapResultSetToDiscount(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch valid discounts for amount: " + e.getMessage(), e);
        }

        return discounts;
    }

    @Override
    public void updateDiscount(Discount discount) {
        String sql = """
            UPDATE discounts SET 
                discount_code = ?, description = ?, discount_type = ?, discount_value = ?,
                min_purchase_amount = ?, max_discount_amount = ?, start_date = ?, end_date = ?,
                is_active = ?, usage_limit = ?, updated_at = CURRENT_TIMESTAMP
            WHERE discount_id = ?
            """;

        try (Connection conn = Db.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, discount.discountCode());
            stmt.setString(2, discount.description());
            stmt.setString(3, discount.discountType().name());
            stmt.setBigDecimal(4, discount.discountValue());
            stmt.setBigDecimal(5, discount.minPurchaseAmount());

            if (discount.maxDiscountAmount() != null) {
                stmt.setBigDecimal(6, discount.maxDiscountAmount());
            } else {
                stmt.setNull(6, Types.DECIMAL);
            }

            stmt.setTimestamp(7, Timestamp.valueOf(discount.startDate()));
            stmt.setTimestamp(8, Timestamp.valueOf(discount.endDate()));
            stmt.setBoolean(9, discount.isActive());

            if (discount.usageLimit() != null) {
                stmt.setInt(10, discount.usageLimit());
            } else {
                stmt.setNull(10, Types.INTEGER);
            }

            stmt.setInt(11, discount.discountId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Discount not found: " + discount.discountId());
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update discount: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteDiscount(int discountId) {
        String sql = "DELETE FROM discounts WHERE discount_id = ?";

        try (Connection conn = Db.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, discountId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Discount not found: " + discountId);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete discount: " + e.getMessage(), e);
        }
    }

    @Override
    public void incrementUsageCount(int discountId) {
        String sql = "UPDATE discounts SET times_used = times_used + 1 WHERE discount_id = ?";

        try (Connection conn = Db.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, discountId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to increment usage count: " + e.getMessage(), e);
        }
    }

    @Override
    public void recordDiscountUsage(int orderId, int discountId, BigDecimal discountAmount) {
        String sql = "INSERT INTO order_discounts (order_id, discount_id, discount_amount) VALUES (?, ?, ?)";

        try (Connection conn = Db.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);
            stmt.setInt(2, discountId);
            stmt.setBigDecimal(3, discountAmount);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to record discount usage: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Discount> findDiscountsByCreator(Long userId) {
        String sql = "SELECT * FROM discounts WHERE created_by = ? ORDER BY created_at DESC";
        List<Discount> discounts = new ArrayList<>();

        try (Connection conn = Db.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    discounts.add(mapResultSetToDiscount(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch discounts by creator: " + e.getMessage(), e);
        }

        return discounts;
    }

    @Override
    public int getTotalActiveDiscounts() {
        String sql = """
            SELECT COUNT(*) FROM discounts 
            WHERE is_active = TRUE 
              AND start_date <= NOW() 
              AND end_date >= NOW()
            """;

        try (Connection conn = Db.get();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to get total active discounts: " + e.getMessage(), e);
        }

        return 0;
    }

    @Override
    public BigDecimal getTotalDiscountsSaved() {
        String sql = "SELECT COALESCE(SUM(discount_amount), 0) FROM order_discounts";

        try (Connection conn = Db.get();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getBigDecimal(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to get total discounts saved: " + e.getMessage(), e);
        }

        return BigDecimal.ZERO;
    }

    private Discount mapResultSetToDiscount(ResultSet rs) throws SQLException {
        return new Discount(
            rs.getInt("discount_id"),
            rs.getString("discount_code"),
            rs.getString("description"),
            DiscountType.valueOf(rs.getString("discount_type")),
            rs.getBigDecimal("discount_value"),
            rs.getBigDecimal("min_purchase_amount"),
            rs.getBigDecimal("max_discount_amount"),
            rs.getTimestamp("start_date").toLocalDateTime(),
            rs.getTimestamp("end_date").toLocalDateTime(),
            rs.getBoolean("is_active"),
            rs.getObject("usage_limit", Integer.class),
            rs.getInt("times_used"),
            rs.getLong("created_by"),
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getTimestamp("updated_at").toLocalDateTime(),
            rs.getString("item_code"),    //  NEW
            rs.getString("category")      //  NEW
        );
    }
}
