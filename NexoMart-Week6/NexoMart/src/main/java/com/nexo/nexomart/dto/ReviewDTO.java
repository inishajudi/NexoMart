package com.nexo.nexomart.dto;

import java.time.LocalDateTime;

public class ReviewDTO {
    private Long id;
    private Long productId;
    private Long userId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;

    public ReviewDTO() { }

    public ReviewDTO(Long id, Long productId, Long userId, Integer rating, String comment, LocalDateTime createdAt) {
        this.id = id;
        this.productId = productId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public Long getUserId() { return userId; }
    public Integer getRating() { return rating; }
    public String getComment() { return comment; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
