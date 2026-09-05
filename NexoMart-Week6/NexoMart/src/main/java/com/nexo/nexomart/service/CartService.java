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

/** Implements F4: add / update / remove cart items, and the running total.
 *  Week 6 hardening: stock checks account for quantity already in the cart (upsert
 *  combines them), not just the newly requested amount, and a sane per-line upper
 *  bound guards against pathological input. The checkout transaction in
 *  OrderDAOImpl.placeOrderFromCart remains the authoritative stock check either way —
 *  these are early, friendlier feedback, not the last line of defense. */
public class CartService {

    /** Upper bound on a single cart line's quantity — not a business requirement, just
     *  a guard against a malformed or abusive request (e.g. quantity=999999999). */
    private static final int MAX_QUANTITY_PER_LINE = 1000;

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
        ValidationUtil.requireRange(quantity, 1, MAX_QUANTITY_PER_LINE, "quantity");

        Product product = productDAO.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product " + productId + " not found"));

        int alreadyInCart = cartDAO.findByUserAndProduct(userId, productId)
                .map(CartItem::getQuantity)
                .orElse(0);
        int totalRequested = alreadyInCart + quantity;
        if (product.getStockQty() < totalRequested) {
            int canStillAdd = Math.max(0, product.getStockQty() - alreadyInCart);
            throw new ValidationException("quantity",
                    "Only " + canStillAdd + " more of this item can be added (stock limit)");
        }

        CartItem saved = cartDAO.upsert(userId, productId, quantity);
        return toDTO(saved);
    }

    public CartItemDTO updateQuantity(long userId, long cartItemId, int quantity)
            throws ValidationException, NotFoundException {
        ValidationUtil.requireRange(quantity, 1, MAX_QUANTITY_PER_LINE, "quantity");

        CartItem existing = cartDAO.findByUser(userId).stream()
                .filter(c -> c.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Cart item " + cartItemId + " not found for this user"));

        Product product = productDAO.findById(existing.getProductId())
                .orElseThrow(() -> new NotFoundException("Product " + existing.getProductId() + " not found"));
        if (product.getStockQty() < quantity) {
            throw new ValidationException("quantity", "Only " + product.getStockQty() + " in stock");
        }

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
