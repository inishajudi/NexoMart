package com.nexo.nexomart.dto;

/** Body of POST /api/v1/cart and PUT /api/v1/cart/{id}. */
public class CartRequestDTO {
    private Long productId;
    private Integer quantity;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
