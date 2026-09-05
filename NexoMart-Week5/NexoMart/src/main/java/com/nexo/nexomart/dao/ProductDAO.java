package com.nexo.nexomart.dao;

import com.nexo.nexomart.model.Product;
import com.nexo.nexomart.dto.ProductSearchCriteria;

import java.util.List;
import java.util.Optional;

public interface ProductDAO {
    Product create(Product product);
    Optional<Product> findById(long id);

    /** Kept for existing callers (Week 2). Prefer the criteria overload below, which adds
     *  price range and sort (Week 5). */
    List<Product> search(String keyword, String category);

    /** Week 5: price range + sort on top of keyword/category. */
    List<Product> search(ProductSearchCriteria criteria);

    List<Product> findAll();
    /** Listings owned by a given seller, for the seller dashboard (Week 3). */
    List<Product> findBySeller(long sellerId);
    boolean updateStock(long productId, int newStockQty);
    boolean update(Product product);
    boolean delete(long id);
}
