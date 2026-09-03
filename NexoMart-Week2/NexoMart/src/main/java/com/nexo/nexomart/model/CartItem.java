package com.nexo.nexomart.model;

import java.time.LocalDateTime;

public class CartItem {
    private Long id;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private LocalDateTime createdAt;

    public CartItem() { }

    public CartItem(Long id, Long userId, Long productId, Integer quantity, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
