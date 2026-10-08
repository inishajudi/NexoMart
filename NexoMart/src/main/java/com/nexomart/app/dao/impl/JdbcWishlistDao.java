package com.nexomart.app.dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import com.nexomart.app.dao.WishlistDao;
import com.nexomart.app.exception.DataAccessException;
import com.nexomart.app.model.Product;

public class JdbcWishlistDao implements WishlistDao {

    private final DataSource dataSource;

    public JdbcWishlistDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void add(long userId, long productId) {
        if (exists(userId, productId)) return;
        String sql = "INSERT INTO wishlist_items (user_id, product_id, created_at) VALUES (?, ?, CURRENT_TIMESTAMP)";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to add to wishlist", e);
        }
    }

    @Override
    public void remove(long userId, long productId) {
        String sql = "DELETE FROM wishlist_items WHERE user_id = ? AND product_id = ?";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to remove from wishlist", e);
        }
    }

    @Override
    public boolean exists(long userId, long productId) {
        String sql = "SELECT 1 FROM wishlist_items WHERE user_id = ? AND product_id = ?";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to check wishlist", e);
        }
    }

    @Override
    public List<Product> findByUser(long userId) {
        String sql = "SELECT p.* FROM products p " +
                     "JOIN wishlist_items w ON w.product_id = p.id " +
                     "WHERE w.user_id = ? ORDER BY w.created_at DESC";
        List<Product> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product p = new Product();
                    p.setId(rs.getLong("id"));
                    p.setSellerId(rs.getLong("seller_id"));
                    p.setName(rs.getString("name"));
                    p.setDescription(rs.getString("description"));
                    p.setPrice(rs.getBigDecimal("price"));
                    p.setStockQty(rs.getInt("stock_qty"));
                    p.setCategory(rs.getString("category"));
                    p.setImageUrl(rs.getString("image_url"));
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load wishlist", e);
        }
        return list;
    }
}