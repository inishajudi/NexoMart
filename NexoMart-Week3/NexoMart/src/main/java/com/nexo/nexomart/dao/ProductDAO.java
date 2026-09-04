package com.nexo.nexomart.dao;

import com.nexo.nexomart.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductDAO {
    Product create(Product product);
    Optional<Product> findById(long id);
    List<Product> search(String keyword, String category);
    List<Product> findAll();
    /** Listings owned by a given seller, for the seller dashboard (Week 3). */
    List<Product> findBySeller(long sellerId);
    boolean updateStock(long productId, int newStockQty);
    boolean update(Product product);
    boolean delete(long id);
}
