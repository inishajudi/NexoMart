package com.nexo.nexomart.dto;

/** Body of POST /api/v1/reviews. */
public class ReviewRequestDTO {
    private Long productId;
    private Integer rating;
    private String comment;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
