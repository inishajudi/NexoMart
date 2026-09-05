package com.nexo.nexomart.service;

import com.nexo.nexomart.dao.CartDAO;
import com.nexo.nexomart.dao.OrderDAO;
import com.nexo.nexomart.exception.ForbiddenException;
import com.nexo.nexomart.exception.NotFoundException;
import com.nexo.nexomart.exception.ValidationException;
import com.nexo.nexomart.model.Order;
import com.nexo.nexomart.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Covers O2 (order status workflow) authorization + transition rules, DAOs mocked. */
class OrderServiceTest {

    private OrderDAO orderDAO;
    private CartDAO cartDAO;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderDAO = mock(OrderDAO.class);
        cartDAO = mock(CartDAO.class);
        orderService = new OrderService(orderDAO, cartDAO);
    }

    private Order orderWith(long buyerId, OrderStatus status) {
        Order o = new Order();
        o.setId(10L);
        o.setBuyerId(buyerId);
        o.setStatus(status);
        o.setTotalAmount(new BigDecimal("100.00"));
        o.setItems(List.of());
        return o;
    }

    @Test
    void sellerCanConfirmPendingOrderTheyHaveAnItemIn() throws Exception {
        when(orderDAO.findById(10L)).thenReturn(Optional.of(orderWith(1L, OrderStatus.PENDING)));
        when(orderDAO.belongsToSeller(10L, 2L)).thenReturn(true);
        when(orderDAO.updateStatus(10L, OrderStatus.CONFIRMED)).thenReturn(true);

        var dto = orderService.advanceStatus(10L, "CONFIRMED", 2L, false);

        assertEquals("CONFIRMED", dto.getStatus());
        verify(orderDAO).updateStatus(10L, OrderStatus.CONFIRMED);
    }

    @Test
    void sellerCannotConfirmOrderTheyHaveNoItemIn() {
        when(orderDAO.findById(10L)).thenReturn(Optional.of(orderWith(1L, OrderStatus.PENDING)));
        when(orderDAO.belongsToSeller(10L, 99L)).thenReturn(false);

        assertThrows(ForbiddenException.class, () -> orderService.advanceStatus(10L, "CONFIRMED", 99L, false));
        verify(orderDAO, never()).updateStatus(anyLong(), any());
    }

    @Test
    void sellerCannotSkipDirectlyFromPendingToShipped() {
        when(orderDAO.findById(10L)).thenReturn(Optional.of(orderWith(1L, OrderStatus.PENDING)));
        when(orderDAO.belongsToSeller(10L, 2L)).thenReturn(true);

        assertThrows(ValidationException.class, () -> orderService.advanceStatus(10L, "SHIPPED", 2L, false));
        verify(orderDAO, never()).updateStatus(anyLong(), any());
    }

    @Test
    void buyerCanCancelTheirOwnPendingOrder() throws Exception {
        when(orderDAO.findById(10L)).thenReturn(Optional.of(orderWith(1L, OrderStatus.PENDING)));
        when(orderDAO.updateStatus(10L, OrderStatus.CANCELLED)).thenReturn(true);

        var dto = orderService.advanceStatus(10L, "CANCELLED", 1L, false);

        assertEquals("CANCELLED", dto.getStatus());
    }

    @Test
    void buyerCannotCancelSomeoneElsesOrder() {
        when(orderDAO.findById(10L)).thenReturn(Optional.of(orderWith(1L, OrderStatus.PENDING)));
        when(orderDAO.belongsToSeller(10L, 5L)).thenReturn(false);

        assertThrows(ForbiddenException.class, () -> orderService.advanceStatus(10L, "CANCELLED", 5L, false));
    }

    @Test
    void cannotCancelAShippedOrder() {
        when(orderDAO.findById(10L)).thenReturn(Optional.of(orderWith(1L, OrderStatus.SHIPPED)));

        assertThrows(ValidationException.class, () -> orderService.advanceStatus(10L, "CANCELLED", 1L, false));
    }

    @Test
    void adminCanAdvanceAnyValidTransition() throws Exception {
        when(orderDAO.findById(10L)).thenReturn(Optional.of(orderWith(1L, OrderStatus.CONFIRMED)));
        when(orderDAO.updateStatus(10L, OrderStatus.SHIPPED)).thenReturn(true);

        var dto = orderService.advanceStatus(10L, "SHIPPED", 999L, true);

        assertEquals("SHIPPED", dto.getStatus());
    }

    @Test
    void unknownStatusStringIsRejected() {
        when(orderDAO.findById(10L)).thenReturn(Optional.of(orderWith(1L, OrderStatus.PENDING)));

        assertThrows(ValidationException.class, () -> orderService.advanceStatus(10L, "NOT_A_STATUS", 1L, true));
    }

    @Test
    void throwsNotFoundForMissingOrder() {
        when(orderDAO.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.advanceStatus(999L, "CONFIRMED", 1L, true));
    }
}
