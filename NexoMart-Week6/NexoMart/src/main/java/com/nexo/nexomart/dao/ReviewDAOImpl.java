package com.nexo.nexomart.dao;

import com.nexo.nexomart.dto.RatingSummary;
import com.nexo.nexomart.exception.DataAccessException;
import com.nexo.nexomart.model.Review;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAOImpl implements ReviewDAO {

    private final DataSource dataSource;

    public ReviewDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Review create(Review r) {
        String sql = "INSERT INTO reviews (product_id, user_id, rating, comment) VALUES (?, ?, ?, ?)";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, r.getProductId());
            ps.setLong(2, r.getUserId());
            ps.setInt(3, r.getRating());
            ps.setString(4, r.getComment());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    r.setId(keys.getLong(1));
                }
            }
            return r;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to insert review", e);
        }
    }

    @Override
    public List<Review> findByProduct(long productId) {
        String sql = "SELECT * FROM reviews WHERE product_id = ? ORDER BY created_at DESC";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Review> reviews = new ArrayList<>();
                while (rs.next()) {
                    reviews.add(mapRow(rs));
                }
                return reviews;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list reviews for product " + productId, e);
        }
    }

    @Override
    public boolean existsForUserAndProduct(long userId, long productId) {
        String sql = "SELECT 1 FROM reviews WHERE user_id = ? AND product_id = ? LIMIT 1";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to check existing review", e);
        }
    }

    @Override
    public boolean hasDeliveredOrderForProduct(long userId, long productId) {
        String sql = "SELECT 1 FROM orders o JOIN order_items oi ON oi.order_id = o.id "
                + "WHERE o.buyer_id = ? AND oi.product_id = ? AND o.status = 'DELIVERED' LIMIT 1";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to check delivered-order eligibility", e);
        }
    }

    @Override
    public RatingSummary getRatingSummary(long productId) {
        String sql = "SELECT AVG(rating) AS avg_rating, COUNT(*) AS review_count FROM reviews WHERE product_id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                double avg = rs.getDouble("avg_rating"); // 0.0 when there are no rows, not null
                long count = rs.getLong("review_count");
                return new RatingSummary(avg, count);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to compute rating summary for product " + productId, e);
        }
    }

    private Review mapRow(ResultSet rs) throws SQLException {
        Review r = new Review();
        r.setId(rs.getLong("id"));
        r.setProductId(rs.getLong("product_id"));
        r.setUserId(rs.getLong("user_id"));
        r.setRating(rs.getInt("rating"));
        r.setComment(rs.getString("comment"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            r.setCreatedAt(ts.toLocalDateTime());
        }
        return r;
    }
}
