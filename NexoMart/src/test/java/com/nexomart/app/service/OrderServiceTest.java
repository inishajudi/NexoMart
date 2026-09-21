package com.nexomart.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nexomart.app.dao.CartDao;
import com.nexomart.app.dao.OrderDao;
import com.nexomart.app.dao.ProductDao;
import com.nexomart.app.exception.BusinessRuleException;
import com.nexomart.app.model.CartItem;
import com.nexomart.app.model.Order;

class OrderServiceTest {

    private OrderDao orderDao;
    private CartDao cartDao;
    private ProductDao productDao;
    private OrderService service;

    @BeforeEach
    void setUp() {
        orderDao = mock(OrderDao.class);
        cartDao = mock(CartDao.class);
        productDao = mock(ProductDao.class);
        service = new OrderService(orderDao, cartDao, productDao);
    }

    private CartItem cartItem(int qty, int stock) {
        CartItem item = mock(CartItem.class);
        when(item.getQuantity()).thenReturn(qty);
        when(item.getAvailableStock()).thenReturn(stock);
        when(item.getProductName()).thenReturn("Test Item");
        when(item.getUnitPrice()).thenReturn(new BigDecimal("100.00"));
        when(item.getLineTotal()).thenReturn(new BigDecimal("200.00"));
        return item;
    }

    @Test
    void checkoutWithEmptyCartThrows() {
        when(cartDao.findByUser(7L)).thenReturn(List.of());
        assertThrows(BusinessRuleException.class, () -> service.checkout(7L));
        verify(orderDao, never()).insertWithItems(any(Order.class));
    }

    @Test
    void checkoutWithInsufficientStockThrowsAndSavesNothing() {
        CartItem item = cartItem(5, 2);
        when(cartDao.findByUser(7L)).thenReturn(List.of(item));
        assertThrows(BusinessRuleException.class, () -> service.checkout(7L));
        verify(orderDao, never()).insertWithItems(any(Order.class));
        verify(cartDao, never()).deleteAllForUser(7L);
    }

    @Test
    void successfulCheckoutConfirmsOrderReducesStockAndClearsCart() throws Exception {
        CartItem item = cartItem(2, 10);
        when(cartDao.findByUser(7L)).thenReturn(List.of(item));
        when(orderDao.insertWithItems(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = service.checkout(7L);

        assertEquals(Order.Status.CONFIRMED, result.getStatus());
        assertEquals(new BigDecimal("200.00"), result.getTotalAmount());
        verify(productDao).decrementStock(anyLong(), anyInt());
        verify(cartDao).deleteAllForUser(7L);
    }
}