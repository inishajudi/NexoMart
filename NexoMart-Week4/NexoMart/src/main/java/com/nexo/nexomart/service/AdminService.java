package com.nexo.nexomart.service;

import com.nexo.nexomart.dao.OrderDAO;
import com.nexo.nexomart.dao.UserDAO;
import com.nexo.nexomart.dto.OrderDTO;
import com.nexo.nexomart.dto.UserResponseDTO;
import com.nexo.nexomart.exception.NotFoundException;
import com.nexo.nexomart.model.Order;
import com.nexo.nexomart.model.OrderItem;
import com.nexo.nexomart.model.User;
import com.nexo.nexomart.dto.CartItemDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Business logic for F7 (admin: view all users and orders; moderate/remove listings).
 * Composes UserDAO / OrderDAO / ProductService rather than owning SQL itself — no JDBC
 * lives in the service layer, per Section 2.
 */
public class AdminService {

    private final UserDAO userDAO;
    private final OrderDAO orderDAO;
    private final ProductService productService;

    public AdminService(UserDAO userDAO, OrderDAO orderDAO, ProductService productService) {
        this.userDAO = userDAO;
        this.orderDAO = orderDAO;
        this.productService = productService;
    }

    public List<UserResponseDTO> listAllUsers() {
        return userDAO.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<OrderDTO> listAllOrders() {
        return orderDAO.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    /** Moderate/remove a listing regardless of which seller owns it. */
    public void removeListing(long productId) throws NotFoundException {
        productService.adminDelete(productId);
    }

    private UserResponseDTO toDTO(User u) {
        return new UserResponseDTO(u.getId(), u.getName(), u.getEmail(),
                u.getRole().name(), u.getCreatedAt());
    }

    private OrderDTO toDTO(Order o) {
        List<CartItemDTO> lines = new ArrayList<>();
        for (OrderItem oi : o.getItems()) {
            lines.add(new CartItemDTO(null, oi.getProductId(), null, oi.getUnitPrice(), oi.getQuantity()));
        }
        return new OrderDTO(o.getId(), o.getStatus().name(), o.getTotalAmount(), o.getCreatedAt(), lines);
    }
}
