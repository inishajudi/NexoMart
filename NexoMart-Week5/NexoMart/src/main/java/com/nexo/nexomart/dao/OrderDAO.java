package com.nexo.nexomart.dao;

import com.nexo.nexomart.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderDAO {
    /** Places an order for everything currently in the buyer's cart: creates the order
     *  row, copies cart lines into order_items at current price, decrements product
     *  stock, and clears the cart — all in one DB transaction. */
    Order placeOrderFromCart(long buyerId);

    Optional<Order> findById(long orderId);

    List<Order> findByBuyer(long buyerId);

    /** Orders containing at least one item whose product belongs to this seller. */
    List<Order> findBySeller(long sellerId);

    /** All orders in the system, for the admin panel (F7). */
    List<Order> findAll();

    /** O2: advances (or cancels) an order's status. Callers must validate the transition
     *  themselves (see util.OrderStatusWorkflow) — this method just persists it. */
    boolean updateStatus(long orderId, com.nexo.nexomart.model.OrderStatus newStatus);

    /** True if at least one item in this order belongs to the given seller — used to
     *  authorize a seller's status-update request. */
    boolean belongsToSeller(long orderId, long sellerId);
}
