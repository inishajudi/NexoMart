package com.nexo.nexomart.service;

import com.nexo.nexomart.dao.ProductDAO;
import com.nexo.nexomart.dto.ProductDTO;
import com.nexo.nexomart.exception.NotFoundException;
import com.nexo.nexomart.exception.ValidationException;
import com.nexo.nexomart.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Service-layer tests with the DAO mocked (per Section 9's testing matrix). Covers the
 * Week 3 ownership rule: a seller can only edit/delete their own listings.
 */
class ProductServiceTest {

    private ProductDAO productDAO;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productDAO = mock(ProductDAO.class);
        productService = new ProductService(productDAO);
    }

    @Test
    void update_succeedsForOwningSeller() throws Exception {
        Product existing = new Product();
        existing.setId(1L);
        existing.setSellerId(42L);
        existing.setName("Old name");
        existing.setPrice(new BigDecimal("10.00"));
        existing.setStockQty(5);
        when(productDAO.findById(1L)).thenReturn(Optional.of(existing));
        when(productDAO.update(any())).thenReturn(true);

        ProductDTO dto = new ProductDTO();
        dto.setName("New name");
        dto.setPrice(new BigDecimal("12.00"));
        dto.setStockQty(3);

        ProductDTO updated = productService.update(42L, 1L, dto);

        assertEquals("New name", updated.getName());
        verify(productDAO).update(any());
    }

    @Test
    void update_throwsNotFoundWhenSellerDoesNotOwnProduct() {
        Product existing = new Product();
        existing.setId(1L);
        existing.setSellerId(42L);
        when(productDAO.findById(1L)).thenReturn(Optional.of(existing));

        ProductDTO dto = new ProductDTO();
        dto.setName("Hijack attempt");
        dto.setPrice(new BigDecimal("1.00"));
        dto.setStockQty(1);

        assertThrows(NotFoundException.class, () -> productService.update(99L, 1L, dto));
        verify(productDAO, never()).update(any());
    }

    @Test
    void delete_throwsNotFoundWhenSellerDoesNotOwnProduct() {
        Product existing = new Product();
        existing.setId(1L);
        existing.setSellerId(42L);
        when(productDAO.findById(1L)).thenReturn(Optional.of(existing));

        assertThrows(NotFoundException.class, () -> productService.delete(99L, 1L));
        verify(productDAO, never()).delete(anyLong());
    }

    @Test
    void update_rejectsInvalidPrice() {
        Product existing = new Product();
        existing.setId(1L);
        existing.setSellerId(42L);
        when(productDAO.findById(1L)).thenReturn(Optional.of(existing));

        ProductDTO dto = new ProductDTO();
        dto.setName("Valid name");
        dto.setPrice(new BigDecimal("-5.00"));
        dto.setStockQty(1);

        assertThrows(ValidationException.class, () -> productService.update(42L, 1L, dto));
    }
}
