package com.nexo.nexomart.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDTO {
    private Long id;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private List<CartItemDTO> items;

    public OrderDTO() { }

    public OrderDTO(Long id, String status, BigDecimal totalAmount, LocalDateTime createdAt,
                     List<CartItemDTO> items) {
        this.id = id;
        this.status = status;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.items = items;
    }

    public Long getId() { return id; }
    public String getStatus() { return status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<CartItemDTO> getItems() { return items; }
}
