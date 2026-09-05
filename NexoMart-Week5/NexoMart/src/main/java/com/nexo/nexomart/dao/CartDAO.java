package com.nexo.nexomart.dao;

import com.nexo.nexomart.model.CartItem;

import java.util.List;
import java.util.Optional;

public interface CartDAO {
    /** Inserts a new cart line, or if the user already has this product in their cart,
     *  increments the existing line's quantity instead (enforced by the DB's
     *  uq_cart_user_product unique constraint). */
    CartItem upsert(long userId, long productId, int quantityToAdd);

    Optional<CartItem> findByUserAndProduct(long userId, long productId);

    List<CartItem> findByUser(long userId);

    boolean updateQuantity(long cartItemId, long userId, int quantity);

    boolean delete(long cartItemId, long userId);

    void clearForUser(long userId);
}
