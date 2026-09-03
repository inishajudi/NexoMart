package com.nexo.nexomart.dao;

import com.nexo.nexomart.exception.DataAccessException;
import com.nexo.nexomart.model.Product;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** All SQL for the products table lives here. PreparedStatement only — no
 *  string-concatenated queries, per Section 2 rule 1. */
public class ProductDAOImpl implements ProductDAO {

    private final DataSource dataSource;

    public ProductDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Product create(Product p) {
        String sql = "INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, p.getSellerId());
            ps.setString(2, p.getName());
            ps.setString(3, p.getDescription());
            ps.setBigDecimal(4, p.getPrice());
            ps.setInt(5, p.getStockQty());
            ps.setString(6, p.getCategory());
            ps.setString(7, p.getImageUrl());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    p.setId(keys.getLong(1));
                }
            }
            return p;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to insert product", e);
        }
    }

    @Override
    public Optional<Product> findById(long id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to fetch product " + id, e);
        }
    }

    @Override
    public List<Product> search(String keyword, String category) {
        StringBuilder sql = new StringBuilder("SELECT * FROM products WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND LOWER(name) LIKE ?");
            params.add("%" + keyword.toLowerCase() + "%");
        }
        if (category != null && !category.isBlank()) {
            sql.append(" AND category = ?");
            params.add(category);
        }
        sql.append(" ORDER BY created_at DESC");

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<Product> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
                return results;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to search products", e);
        }
    }

    @Override
    public List<Product> findAll() {
        String sql = "SELECT * FROM products ORDER BY created_at DESC";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Product> results = new ArrayList<>();
            while (rs.next()) {
                results.add(mapRow(rs));
            }
            return results;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list products", e);
        }
    }

    @Override
    public boolean updateStock(long productId, int newStockQty) {
        String sql = "UPDATE products SET stock_qty = ? WHERE id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, newStockQty);
            ps.setLong(2, productId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update stock for product " + productId, e);
        }
    }

    @Override
    public boolean update(Product p) {
        String sql = "UPDATE products SET name=?, description=?, price=?, stock_qty=?, category=?, image_url=? "
                + "WHERE id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            ps.setBigDecimal(3, p.getPrice());
            ps.setInt(4, p.getStockQty());
            ps.setString(5, p.getCategory());
            ps.setString(6, p.getImageUrl());
            ps.setLong(7, p.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update product " + p.getId(), e);
        }
    }

    @Override
    public boolean delete(long id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete product " + id, e);
        }
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getLong("id"));
        p.setSellerId(rs.getLong("seller_id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setStockQty(rs.getInt("stock_qty"));
        p.setCategory(rs.getString("category"));
        p.setImageUrl(rs.getString("image_url"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            p.setCreatedAt(ts.toLocalDateTime());
        }
        return p;
    }
}
