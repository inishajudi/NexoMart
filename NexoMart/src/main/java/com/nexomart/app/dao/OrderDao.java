package com.nexomart.app.dao;

import java.util.List;
import java.util.Optional;

import com.nexomart.app.model.Order;

public interface OrderDao {

    /** Persists the order and all of its order_items in a single transaction. */
    Order insertWithItems(Order order);

    Optional<Order> findByIdWithItems(long orderId);

    List<Order> findByBuyer(long buyerId);

    List<Order> findAll();

    /**
     * Returns orders that contain at least one item whose product belongs to the given seller.
     * Each returned Order includes only the order_items belonging to that seller's products.
     */
    List<Order> findBySeller(long sellerId);

    /**
     * Returns true if the given buyer has a DELIVERED order containing the given product.
     * Used to gate review eligibility per the spec (F8: reviews on completed orders).
     */
    boolean hasDeliveredOrderForProduct(long buyerId, long productId);
}