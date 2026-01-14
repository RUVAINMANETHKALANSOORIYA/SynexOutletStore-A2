package infrastructure.jdbc;

import domain.store.Item;
import ports.out.ItemRepository;

import java.sql.*;
import java.util.*;
import java.math.BigDecimal;

public final class JdbcItemRepository implements ItemRepository {

    @Override
    public List<Item> findAllActive() {
        String sql = "SELECT item_code, name, price FROM items WHERE is_active=1 ORDER BY name";
        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Item> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new Item(
                        rs.getString("item_code"),
                        rs.getString("name"),
                        rs.getBigDecimal("price")
                ));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("findAllActive failed", e);
        }
    }

    @Override
    public Optional<Item> findByCode(String itemCode) {
        String sql = "SELECT item_code, name, price FROM items WHERE item_code=? AND is_active=1";
        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, itemCode);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(new Item(
                        rs.getString("item_code"),
                        rs.getString("name"),
                        rs.getBigDecimal("price")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException("findByCode failed", e);
        }
    }
}
