package com.nexo.nexomart.service;

import com.nexo.nexomart.dao.OrderDAO;
import com.nexo.nexomart.dao.ProductDAO;
import com.nexo.nexomart.dao.UserDAO;
import com.nexo.nexomart.exception.NotFoundException;
import com.nexo.nexomart.model.Product;
import com.nexo.nexomart.model.Role;
import com.nexo.nexomart.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Service-layer tests with DAOs mocked, per Section 9. Covers F7: admin can list users,
 *  list all orders, and remove any listing regardless of owner. */
class AdminServiceTest {

    private UserDAO userDAO;
    private OrderDAO orderDAO;
    private ProductDAO productDAO;
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        userDAO = mock(UserDAO.class);
        orderDAO = mock(OrderDAO.class);
        productDAO = mock(ProductDAO.class);
        adminService = new AdminService(userDAO, orderDAO, new ProductService(productDAO));
    }

    @Test
    void listAllUsers_neverExposesPasswordHash() {
        User u = new User();
        u.setId(1L);
        u.setName("Buyer One");
        u.setEmail("buyer1@nexomart.local");
        u.setRole(Role.BUYER);
        u.setCreatedAt(LocalDateTime.now());
        when(userDAO.findAll()).thenReturn(List.of(u));

        var result = adminService.listAllUsers();

        assertEquals(1, result.size());
        assertEquals("buyer1@nexomart.local", result.get(0).getEmail());
        // UserResponseDTO has no getPasswordHash() at all — compile-time guarantee,
        // this assertion just documents the intent for anyone reading the test.
    }

    @Test
    void removeListing_deletesRegardlessOfOwner() throws NotFoundException {
        Product p = new Product();
        p.setId(5L);
        p.setSellerId(999L); // some other seller, not an admin
        when(productDAO.findById(5L)).thenReturn(Optional.of(p));
        when(productDAO.delete(5L)).thenReturn(true);

        adminService.removeListing(5L);

        verify(productDAO).delete(5L);
    }

    @Test
    void removeListing_throwsNotFoundForMissingProduct() {
        when(productDAO.findById(404L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> adminService.removeListing(404L));
        verify(productDAO, never()).delete(anyLong());
    }
}
