package com.nexo.nexomart.service;

import com.nexo.nexomart.dao.ProductDAO;
import com.nexo.nexomart.dao.ReviewDAO;
import com.nexo.nexomart.dto.RatingSummary;
import com.nexo.nexomart.dto.ReviewDTO;
import com.nexo.nexomart.exception.NotFoundException;
import com.nexo.nexomart.exception.ValidationException;
import com.nexo.nexomart.model.Review;
import com.nexo.nexomart.util.ValidationUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * F8: product reviews and star ratings on completed orders. A review requires:
 *   1. rating in [1,5], comment within the schema's 2000-char limit
 *   2. the reviewer has at least one DELIVERED order containing this product
 *   3. the reviewer hasn't already reviewed this product (DB unique constraint backs
 *      this up too, per V3__add_review_constraints.sql, so a race loses to the DB, not
 *      silently to a duplicate row)
 */
public class ReviewService {

    private final ReviewDAO reviewDAO;
    private final ProductDAO productDAO;

    public ReviewService(ReviewDAO reviewDAO, ProductDAO productDAO) {
        this.reviewDAO = reviewDAO;
        this.productDAO = productDAO;
    }

    public ReviewDTO submitReview(long userId, Long productId, Integer rating, String comment)
            throws ValidationException, NotFoundException {
        if (productId == null) {
            throw new ValidationException("productId", "productId is required");
        }
        ValidationUtil.requireRange(rating, 1, 5, "rating");
        ValidationUtil.requireMaxLength(comment, 2000, "comment");

        productDAO.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product " + productId + " not found"));

        if (!reviewDAO.hasDeliveredOrderForProduct(userId, productId)) {
            throw new ValidationException("productId",
                    "You can only review products from a delivered order");
        }
        if (reviewDAO.existsForUserAndProduct(userId, productId)) {
            throw new ValidationException("productId", "You have already reviewed this product");
        }

        Review review = new Review();
        review.setProductId(productId);
        review.setUserId(userId);
        review.setRating(rating);
        review.setComment(comment);

        Review saved = reviewDAO.create(review);
        return toDTO(saved);
    }

    public List<ReviewDTO> listForProduct(long productId) {
        return reviewDAO.findByProduct(productId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public RatingSummary ratingSummary(long productId) {
        return reviewDAO.getRatingSummary(productId);
    }

    private ReviewDTO toDTO(Review r) {
        return new ReviewDTO(r.getId(), r.getProductId(), r.getUserId(), r.getRating(),
                r.getComment(), r.getCreatedAt());
    }
}
