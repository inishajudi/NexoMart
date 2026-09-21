package com.nexomart.app.service;

import java.time.LocalDateTime;
import java.util.List;

import com.nexomart.app.dao.OrderDao;
import com.nexomart.app.dao.ReviewDao;
import com.nexomart.app.exception.BusinessRuleException;
import com.nexomart.app.exception.ValidationException;
import com.nexomart.app.model.Review;
import com.nexomart.app.util.ValidationUtil;

public class ReviewService {

    private final ReviewDao reviewDao;
    private final OrderDao orderDao;

    public ReviewService(ReviewDao reviewDao, OrderDao orderDao) {
        this.reviewDao = reviewDao;
        this.orderDao = orderDao;
    }

    /**
     * Submits a review for a product. Only allowed if the buyer has an order
     * containing this product that has reached a completed-enough status, and
     * only one review per buyer per product is allowed.
     */
    public Review submitReview(long userId, long productId, int rating, String comment)
            throws ValidationException, BusinessRuleException {
        ValidationUtil.requireNonNegative(rating - 1, "Rating");
        if (rating < 1 || rating > 5) {
            throw new ValidationException("Rating must be between 1 and 5.");
        }

        if (!orderDao.hasDeliveredOrderForProduct(userId, productId)) {
            throw new BusinessRuleException("You can only review products from your completed orders.");
        }

        if (reviewDao.existsByUserAndProduct(userId, productId)) {
            throw new BusinessRuleException("You have already reviewed this product.");
        }

        Review review = new Review();
        review.setUserId(userId);
        review.setProductId(productId);
        review.setRating(rating);
        review.setComment(comment == null ? "" : comment.trim());
        review.setCreatedAt(LocalDateTime.now());

        return reviewDao.insert(review);
    }

    public List<Review> getReviewsForProduct(long productId) {
        return reviewDao.findByProduct(productId);
    }

    public Double getAverageRating(long productId) {
        return reviewDao.averageRatingForProduct(productId);
    }
}