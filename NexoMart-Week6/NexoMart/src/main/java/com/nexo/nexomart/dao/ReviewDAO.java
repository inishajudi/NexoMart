package com.nexo.nexomart.dao;

import com.nexo.nexomart.dto.RatingSummary;
import com.nexo.nexomart.model.Review;

import java.util.List;

public interface ReviewDAO {
    Review create(Review review);
    List<Review> findByProduct(long productId);
    boolean existsForUserAndProduct(long userId, long productId);

    /** F8's "on completed orders" rule: true only if this user has at least one
     *  DELIVERED order containing this product. */
    boolean hasDeliveredOrderForProduct(long userId, long productId);

    RatingSummary getRatingSummary(long productId);
}
