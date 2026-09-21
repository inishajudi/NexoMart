package com.nexomart.app.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.nexomart.app.dao.CartDao;
import com.nexomart.app.dao.OrderDao;
import com.nexomart.app.dao.ProductDao;
import com.nexomart.app.exception.BusinessRuleException;
import com.nexomart.app.model.CartItem;
import com.nexomart.app.model.Order;
import com.nexomart.app.model.OrderItem;

public class OrderService {

    private final OrderDao orderDao;
    private final CartDao cartDao;
    private final ProductDao productDao;

    public OrderService(OrderDao orderDao, CartDao cartDao, ProductDao productDao) {
        this.orderDao = orderDao;
        this.cartDao = cartDao;
        this.productDao = productDao;
    }

    /**
     * Places an order from the buyer's current cart contents and simulates a payment
     * confirmation (no real payment gateway is involved). On success, stock is
     * decremented, the order is persisted as CONFIRMED, and the cart is cleared.
     */
    public Order checkout(long buyerId) throws BusinessRuleException {
        List<CartItem> cartItems = cartDao.findByUser(buyerId);
        if (cartItems.isEmpty()) {
            throw new BusinessRuleException("Your cart is empty.");
        }

        // Re-validate stock at checkout time in case it changed since items were added.
        for (CartItem item : cartItems) {
            if (item.getQuantity() > item.getAvailableStock()) {
                throw new BusinessRuleException("\"" + item.getProductName()
                        + "\" only has " + item.getAvailableStock() + " unit(s) left in stock.");
            }
        }

        Order order = new Order();
        order.setBuyerId(buyerId);
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> orderItems = new ArrayList<>();
        java.math.BigDecimal total = java.math.BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(item.getProductId());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setUnitPrice(item.getUnitPrice());
            orderItems.add(orderItem);
            total = total.add(item.getLineTotal());
        }
        order.setItems(orderItems);
        order.setTotalAmount(total);

        // --- Mock payment confirmation step ---
        // In a real system this is where a payment gateway callback would arrive.
        // Here we simply mark the order confirmed immediately.
        order.setStatus(Order.Status.CONFIRMED);

        Order savedOrder = orderDao.insertWithItems(order);

        for (CartItem item : cartItems) {
            productDao.decrementStock(item.getProductId(), item.getQuantity());
        }
        cartDao.deleteAllForUser(buyerId);

        return savedOrder;
    }

    public List<Order> getOrderHistory(long buyerId) {
        return orderDao.findByBuyer(buyerId);
    }

    public Optional<Order> getOrder(long orderId) {
        return orderDao.findByIdWithItems(orderId);
    }

    public List<Order> findAllOrders() {
        return orderDao.findAll();
    }

    /**
     * Returns orders that include at least one of the given seller's products.
     * Each order's item list is restricted to just that seller's items.
     */
    public List<Order> getSellerOrders(long sellerId) {
        return orderDao.findBySeller(sellerId);
    }
}