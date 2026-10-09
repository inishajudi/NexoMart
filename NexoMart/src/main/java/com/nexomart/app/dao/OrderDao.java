package com.nexomart.app.dao;

import java.util.List;
import java.util.Optional;

import com.nexomart.app.dto.SellerDashboardDTO;
import com.nexomart.app.model.Order;

public interface OrderDao {

    /** Persists the order and all of its order_items in a single transaction. */
    Order insertWithItems(Order order);

    Optional<Order> findByIdWithItems(long orderId);

    List<Order> findByBuyer(long buyerId);

    List<Order> findAll();

    List<Order> findBySeller(long sellerId);

    boolean hasDeliveredOrderForProduct(long buyerId, long productId);

    void updateStatus(long orderId, Order.Status status);

    SellerDashboardDTO getSellerDashboard(long sellerId);
}
