package com.nexo.nexomart.service;

import com.nexo.nexomart.dao.CartDAO;
import com.nexo.nexomart.dao.OrderDAO;
import com.nexo.nexomart.dto.CartItemDTO;
import com.nexo.nexomart.dto.OrderDTO;
import com.nexo.nexomart.exception.NotFoundException;
import com.nexo.nexomart.exception.ValidationException;
import com.nexo.nexomart.model.Order;
import com.nexo.nexomart.model.OrderItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** Implements F5 (checkout via mock payment confirmation) and half of F6 (buyer/seller
 *  order history). "Mock payment confirmation" per the scope constraints means: no
 *  external gateway call — the client confirms, this method commits the order. */
public class OrderService {

    private final OrderDAO orderDAO;
    private final CartDAO cartDAO;

    public OrderService(OrderDAO orderDAO, CartDAO cartDAO) {
        this.orderDAO = orderDAO;
        this.cartDAO = cartDAO;
    }

    public OrderDTO placeOrder(long buyerId, boolean mockPaymentConfirmed) throws ValidationException {
        if (!mockPaymentConfirmed) {
            throw new ValidationException("paymentConfirmed", "Mock payment confirmation is required to place an order");
        }
        if (cartDAO.findByUser(buyerId).isEmpty()) {
            throw new ValidationException("cart", "Cart is empty");
        }
        Order order = orderDAO.placeOrderFromCart(buyerId);
        return toDTO(order);
    }

    public List<OrderDTO> buyerHistory(long buyerId) {
        return orderDAO.findByBuyer(buyerId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<OrderDTO> sellerIncomingOrders(long sellerId) {
        return orderDAO.findBySeller(sellerId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public OrderDTO getOrder(long orderId, long requestingUserId, boolean isAdmin) throws NotFoundException {
        Order order = orderDAO.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order " + orderId + " not found"));
        if (!isAdmin && order.getBuyerId() != requestingUserId) {
            throw new NotFoundException("Order " + orderId + " not found"); // avoid leaking existence
        }
        return toDTO(order);
    }

    private OrderDTO toDTO(Order o) {
        List<CartItemDTO> lines = new ArrayList<>();
        for (OrderItem oi : o.getItems()) {
            lines.add(new CartItemDTO(null, oi.getProductId(), null, oi.getUnitPrice(), oi.getQuantity()));
        }
        return new OrderDTO(o.getId(), o.getStatus().name(), o.getTotalAmount(), o.getCreatedAt(), lines);
    }
}
