package com.nexo.nexomart.service;

import com.nexo.nexomart.dao.CartDAO;
import com.nexo.nexomart.dao.ProductDAO;
import com.nexo.nexomart.dto.CartItemDTO;
import com.nexo.nexomart.exception.NotFoundException;
import com.nexo.nexomart.exception.ValidationException;
import com.nexo.nexomart.model.CartItem;
import com.nexo.nexomart.model.Product;
import com.nexo.nexomart.util.ValidationUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/** Implements F4: add / update / remove cart items, and the running total. */
public class CartService {

    private final CartDAO cartDAO;
    private final ProductDAO productDAO;

    public CartService(CartDAO cartDAO, ProductDAO productDAO) {
        this.cartDAO = cartDAO;
        this.productDAO = productDAO;
    }

    public List<CartItemDTO> viewCart(long userId) {
        return cartDAO.findByUser(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public BigDecimal runningTotal(long userId) {
        return viewCart(userId).stream()
                .map(CartItemDTO::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public CartItemDTO addItem(long userId, long productId, int quantity)
            throws ValidationException, NotFoundException {
        ValidationUtil.requirePositive(quantity, "quantity");

        Product product = productDAO.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product " + productId + " not found"));
        if (product.getStockQty() < quantity) {
            throw new ValidationException("quantity", "Only " + product.getStockQty() + " in stock");
        }

        CartItem saved = cartDAO.upsert(userId, productId, quantity);
        return toDTO(saved);
    }

    public CartItemDTO updateQuantity(long userId, long cartItemId, int quantity)
            throws ValidationException, NotFoundException {
        ValidationUtil.requirePositive(quantity, "quantity");

        boolean updated = cartDAO.updateQuantity(cartItemId, userId, quantity);
        if (!updated) {
            throw new NotFoundException("Cart item " + cartItemId + " not found for this user");
        }
        return cartDAO.findByUser(userId).stream()
                .filter(c -> c.getId().equals(cartItemId))
                .findFirst()
                .map(this::toDTO)
                .orElseThrow(() -> new NotFoundException("Cart item " + cartItemId + " not found"));
    }

    public void removeItem(long userId, long cartItemId) throws NotFoundException {
        boolean removed = cartDAO.delete(cartItemId, userId);
        if (!removed) {
            throw new NotFoundException("Cart item " + cartItemId + " not found for this user");
        }
    }

    private CartItemDTO toDTO(CartItem c) {
        Product product = productDAO.findById(c.getProductId())
                .orElseThrow(() -> new IllegalStateException(
                        "Cart references missing product " + c.getProductId()));
        return new CartItemDTO(c.getId(), c.getProductId(), product.getName(),
                product.getPrice(), c.getQuantity());
    }
}
