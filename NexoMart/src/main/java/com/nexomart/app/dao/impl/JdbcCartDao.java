package com.nexomart.app.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import com.nexomart.app.dao.CartDao;
import com.nexomart.app.exception.DataAccessException;
import com.nexomart.app.model.CartItem;

public class JdbcCartDao implements CartDao {

    private final DataSource dataSource;

    public JdbcCartDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public CartItem insert(CartItem item) {
        String sql = "INSERT INTO cart_items (user_id, product_id, quantity, created_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, item.getUserId());
            ps.setLong(2, item.getProductId());
            ps.setInt(3, item.getQuantity());
            ps.setTimestamp(4, Timestamp.valueOf(java.time.LocalDateTime.now()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    item.setId(keys.getLong(1));
                }
            }
            return item;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to insert cart item", e);
        }
    }

    @Override
    public Optional<CartItem> findByUserAndProduct(long userId, long productId) {
        String sql = "SELECT id, user_id, product_id, quantity FROM cart_items WHERE user_id = ? AND product_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find cart item", e);
        }
    }

    @Override
    public List<CartItem> findByUser(long userId) {
        String sql = "SELECT ci.id, ci.user_id, ci.product_id, ci.quantity, "
                + "p.name AS product_name, p.price AS unit_price, p.stock_qty AS available_stock "
                + "FROM cart_items ci JOIN products p ON ci.product_id = p.id "
                + "WHERE ci.user_id = ? ORDER BY ci.id";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                List<CartItem> results = new ArrayList<>();
                while (rs.next()) {
                    CartItem item = mapRow(rs);
                    item.setProductName(rs.getString("product_name"));
                    item.setUnitPrice(rs.getBigDecimal("unit_price"));
                    item.setAvailableStock(rs.getInt("available_stock"));
                    results.add(item);
                }
                return results;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to fetch cart for user", e);
        }
    }

    @Override
    public void updateQuantity(long cartItemId, int newQuantity) {
        String sql = "UPDATE cart_items SET quantity = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newQuantity);
            ps.setLong(2, cartItemId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update cart item quantity", e);
        }
    }

    @Override
    public void delete(long cartItemId) {
        String sql = "DELETE FROM cart_items WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, cartItemId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete cart item", e);
        }
    }

    @Override
    public void deleteAllForUser(long userId) {
        String sql = "DELETE FROM cart_items WHERE user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear cart", e);
        }
    }

    private CartItem mapRow(ResultSet rs) throws SQLException {
        CartItem item = new CartItem();
        item.setId(rs.getLong("id"));
        item.setUserId(rs.getLong("user_id"));
        item.setProductId(rs.getLong("product_id"));
        item.setQuantity(rs.getInt("quantity"));
        return item;
    }
}