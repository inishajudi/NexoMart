package com.nexo.nexomart.service;

import com.nexo.nexomart.dao.CartDAO;
import com.nexo.nexomart.dao.ProductDAO;
import com.nexo.nexomart.exception.NotFoundException;
import com.nexo.nexomart.exception.ValidationException;
import com.nexo.nexomart.model.CartItem;
import com.nexo.nexomart.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Week 6 edge-case fix: stock checks must account for quantity already in the cart,
 *  not just the newly requested amount (upsert combines them). */
class CartServiceTest {

    private CartDAO cartDAO;
    private ProductDAO productDAO;
    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartDAO = mock(CartDAO.class);
        productDAO = mock(ProductDAO.class);
        cartService = new CartService(cartDAO, productDAO);
    }

    private Product productWithStock(int stock) {
        Product p = new Product();
        p.setId(1L);
        p.setName("Widget");
        p.setPrice(new BigDecimal("10.00"));
        p.setStockQty(stock);
        return p;
    }

    @Test
    void addItem_rejectsWhenCumulativeQuantityExceedsStock() {
        when(productDAO.findById(1L)).thenReturn(Optional.of(productWithStock(5)));
        CartItem existing = new CartItem();
        existing.setId(50L);
        existing.setProductId(1L);
        existing.setQuantity(4); // 4 already in cart
        when(cartDAO.findByUserAndProduct(9L, 1L)).thenReturn(Optional.of(existing));

        // Requesting 3 more would total 7, but stock is only 5.
        assertThrows(ValidationException.class, () -> cartService.addItem(9L, 1L, 3));
        verify(cartDAO, never()).upsert(anyLong(), anyLong(), anyInt());
    }

    @Test
    void addItem_allowsWhenCumulativeQuantityIsWithinStock() throws Exception {
        when(productDAO.findById(1L)).thenReturn(Optional.of(productWithStock(5)));
        when(cartDAO.findByUserAndProduct(9L, 1L)).thenReturn(Optional.empty());
        CartItem saved = new CartItem();
        saved.setId(50L);
        saved.setProductId(1L);
        saved.setQuantity(3);
        when(cartDAO.upsert(9L, 1L, 3)).thenReturn(saved);

        var dto = cartService.addItem(9L, 1L, 3);

        assertEquals(3, dto.getQuantity());
    }

    @Test
    void addItem_rejectsAbsurdQuantity() {
        assertThrows(ValidationException.class, () -> cartService.addItem(9L, 1L, 1_000_000));
    }

    @Test
    void updateQuantity_rejectsWhenExceedsStock() {
        CartItem existing = new CartItem();
        existing.setId(50L);
        existing.setProductId(1L);
        existing.setQuantity(2);
        when(cartDAO.findByUser(9L)).thenReturn(java.util.List.of(existing));
        when(productDAO.findById(1L)).thenReturn(Optional.of(productWithStock(5)));

        assertThrows(ValidationException.class, () -> cartService.updateQuantity(9L, 50L, 10));
        verify(cartDAO, never()).updateQuantity(anyLong(), anyLong(), anyInt());
    }

    @Test
    void updateQuantity_throwsNotFoundForMissingCartItem() {
        when(cartDAO.findByUser(9L)).thenReturn(java.util.List.of());
        assertThrows(NotFoundException.class, () -> cartService.updateQuantity(9L, 999L, 1));
    }
}
