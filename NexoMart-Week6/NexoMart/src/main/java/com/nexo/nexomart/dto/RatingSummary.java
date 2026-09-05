package com.nexo.nexomart.dto;

/** Aggregate rating info for a product, shown on the product detail page. */
public class RatingSummary {
    private final double averageRating;
    private final long reviewCount;

    public RatingSummary(double averageRating, long reviewCount) {
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
    }

    public double getAverageRating() { return averageRating; }
    public long getReviewCount() { return reviewCount; }
}
