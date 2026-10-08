package com.nexomart.app.dao;

import java.util.List;
import com.nexomart.app.model.Product;

public interface WishlistDao {
    void add(long userId, long productId);
    void remove(long userId, long productId);
    boolean exists(long userId, long productId);
    List<Product> findByUser(long userId);
}