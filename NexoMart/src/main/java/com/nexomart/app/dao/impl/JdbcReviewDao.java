package com.nexomart.app.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.nexomart.app.dao.ReviewDao;
import com.nexomart.app.exception.DataAccessException;
import com.nexomart.app.model.Review;

public class JdbcReviewDao implements ReviewDao {

    private final DataSource dataSource;

    public JdbcReviewDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Review insert(Review review) {
        String sql = "INSERT INTO reviews (product_id, user_id, rating, comment, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, review.getProductId());
            ps.setLong(2, review.getUserId());
            ps.setInt(3, review.getRating());
            ps.setString(4, review.getComment());
            ps.setTimestamp(5, Timestamp.valueOf(review.getCreatedAt()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    review.setId(keys.getLong(1));
                }
            }
            return review;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to insert review", e);
        }
    }

    @Override
    public List<Review> findByProduct(long productId) {
        String sql = "SELECT r.id, r.product_id, r.user_id, r.rating, r.comment, r.created_at, u.name AS reviewer_name "
                + "FROM reviews r JOIN users u ON r.user_id = u.id "
                + "WHERE r.product_id = ? ORDER BY r.created_at DESC";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Review> results = new ArrayList<>();
                while (rs.next()) {
                    Review review = mapRow(rs);
                    review.setReviewerName(rs.getString("reviewer_name"));
                    results.add(review);
                }
                return results;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to fetch reviews for product", e);
        }
    }

    @Override
    public boolean existsByUserAndProduct(long userId, long productId) {
        String sql = "SELECT COUNT(*) FROM reviews WHERE user_id = ? AND product_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to check existing review", e);
        }
    }

    @Override
    public Double averageRatingForProduct(long productId) {
        String sql = "SELECT AVG(rating) FROM reviews WHERE product_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double avg = rs.getDouble(1);
                    return rs.wasNull() ? null : avg;
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to compute average rating", e);
        }
    }

    private Review mapRow(ResultSet rs) throws SQLException {
        Review review = new Review();
        review.setId(rs.getLong("id"));
        review.setProductId(rs.getLong("product_id"));
        review.setUserId(rs.getLong("user_id"));
        review.setRating(rs.getInt("rating"));
        review.setComment(rs.getString("comment"));
        review.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return review;
    }
}