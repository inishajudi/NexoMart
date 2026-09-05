package com.nexo.nexomart.dao;

import com.nexo.nexomart.exception.DataAccessException;
import com.nexo.nexomart.model.CartItem;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CartDAOImpl implements CartDAO {

    private final DataSource dataSource;

    public CartDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public CartItem upsert(long userId, long productId, int quantityToAdd) {
        // MERGE keeps this atomic at the DB level instead of a read-then-write race.
        String sql = "MERGE INTO cart_items (user_id, product_id, quantity) KEY (user_id, product_id) "
                + "VALUES (?, ?, COALESCE((SELECT quantity FROM cart_items WHERE user_id = ? AND product_id = ?), 0) + ?)";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            ps.setLong(3, userId);
            ps.setLong(4, productId);
            ps.setInt(5, quantityToAdd);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to upsert cart item", e);
        }
        return findByUserAndProduct(userId, productId)
                .orElseThrow(() -> new DataAccessException("Cart upsert did not persist", null));
    }

    @Override
    public Optional<CartItem> findByUserAndProduct(long userId, long productId) {
        String sql = "SELECT * FROM cart_items WHERE user_id = ? AND product_id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to fetch cart item", e);
        }
    }

    @Override
    public List<CartItem> findByUser(long userId) {
        String sql = "SELECT * FROM cart_items WHERE user_id = ? ORDER BY created_at";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                List<CartItem> items = new ArrayList<>();
                while (rs.next()) {
                    items.add(mapRow(rs));
                }
                return items;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list cart for user " + userId, e);
        }
    }

    @Override
    public boolean updateQuantity(long cartItemId, long userId, int quantity) {
        String sql = "UPDATE cart_items SET quantity = ? WHERE id = ? AND user_id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setLong(2, cartItemId);
            ps.setLong(3, userId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update cart item " + cartItemId, e);
        }
    }

    @Override
    public boolean delete(long cartItemId, long userId) {
        String sql = "DELETE FROM cart_items WHERE id = ? AND user_id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, cartItemId);
            ps.setLong(2, userId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete cart item " + cartItemId, e);
        }
    }

    @Override
    public void clearForUser(long userId) {
        String sql = "DELETE FROM cart_items WHERE user_id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear cart for user " + userId, e);
        }
    }

    private CartItem mapRow(ResultSet rs) throws SQLException {
        CartItem c = new CartItem();
        c.setId(rs.getLong("id"));
        c.setUserId(rs.getLong("user_id"));
        c.setProductId(rs.getLong("product_id"));
        c.setQuantity(rs.getInt("quantity"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            c.setCreatedAt(ts.toLocalDateTime());
        }
        return c;
    }
}
