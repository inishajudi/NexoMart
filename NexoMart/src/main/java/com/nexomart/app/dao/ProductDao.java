package com.nexomart.app.dao;

import java.util.List;
import java.util.Optional;

import com.nexomart.app.model.Product;

public interface ProductDao {

    Product insert(Product product);

    Optional<Product> findById(long id);

    /**
     * Search active products by optional keyword (matched against name/description)
     * and optional category. Either may be null/blank to skip that filter.
     */
    List<Product> search(String keyword, String category);

    List<Product> findAll();

    void decrementStock(long productId, int quantityToSubtract);

    /**
     * Updates an existing product's editable fields.
     * Returns true if a row was updated, false if no product with that id exists.
     */
    boolean update(Product product);

    /**
     * Deletes a product by id.
     * Returns true if a row was deleted, false if no product with that id exists.
     */
    boolean delete(long id);

    /**
     * Returns true if at least one order_item references this product,
     * meaning it cannot be safely deleted.
     */
    boolean hasExistingOrders(long productId);
}