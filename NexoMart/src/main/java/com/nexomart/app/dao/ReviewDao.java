package com.nexomart.app.dao;

import java.util.List;

import com.nexomart.app.model.Review;

public interface ReviewDao {

    Review insert(Review review);

    /** Returns all reviews for a product, most recent first, with reviewer name joined in. */
    List<Review> findByProduct(long productId);

    /**
     * Returns true if this user has already reviewed this product.
     * Used to prevent duplicate reviews on the same product by the same buyer.
     */
    boolean existsByUserAndProduct(long userId, long productId);

    /** Average rating for a product, or null if it has no reviews yet. */
    Double averageRatingForProduct(long productId);
}