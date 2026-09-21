package com.nexomart.app.dao;

import java.util.List;
import java.util.Optional;

import com.nexomart.app.model.CartItem;

public interface CartDao {

    CartItem insert(CartItem item);

    Optional<CartItem> findByUserAndProduct(long userId, long productId);

    List<CartItem> findByUser(long userId);

    void updateQuantity(long cartItemId, int newQuantity);

    void delete(long cartItemId);

    void deleteAllForUser(long userId);
}
