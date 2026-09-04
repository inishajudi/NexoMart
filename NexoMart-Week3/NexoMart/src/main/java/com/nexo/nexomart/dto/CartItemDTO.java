package com.nexo.nexomart.dto;

import java.math.BigDecimal;

/** A cart line enriched with product display fields, for rendering the cart page /
 *  cart AJAX response without a second client-side lookup. */
public class CartItemDTO {
    private Long cartItemId;
    private Long productId;
    private String productName;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal lineTotal;

    public CartItemDTO() { }

    public CartItemDTO(Long cartItemId, Long productId, String productName,
                        BigDecimal unitPrice, Integer quantity) {
        this.cartItemId = cartItemId;
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public Long getCartItemId() { return cartItemId; }
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getLineTotal() { return lineTotal; }
}
