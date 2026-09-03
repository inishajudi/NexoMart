package com.nexo.nexomart.controller;

import com.nexo.nexomart.dao.CartDAO;
import com.nexo.nexomart.dao.CartDAOImpl;
import com.nexo.nexomart.dao.OrderDAO;
import com.nexo.nexomart.dao.OrderDAOImpl;
import com.nexo.nexomart.dto.ApiResponse;
import com.nexo.nexomart.dto.OrderDTO;
import com.nexo.nexomart.exception.DataAccessException;
import com.nexo.nexomart.exception.NotFoundException;
import com.nexo.nexomart.exception.ValidationException;
import com.nexo.nexomart.listener.DataSourceProvider;
import com.nexo.nexomart.service.OrderService;
import com.nexo.nexomart.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * POST /api/v1/orders                 -> place order from cart (F5), body {paymentConfirmed: true}
 * GET  /api/v1/orders                 -> buyer's own order history (F6)
 * GET  /api/v1/orders/{id}             -> single order detail (buyer-owned or admin)
 * GET  /api/v1/orders?as=seller       -> seller's incoming orders (F6)
 *
 * Sits behind AuthFilter.
 */
@WebServlet("/api/v1/orders/*")
public class OrderServlet extends HttpServlet {

    private OrderService orderService;

    @Override
    public void init() throws ServletException {
        var ds = DataSourceProvider.get(getServletContext());
        OrderDAO orderDAO = new OrderDAOImpl(ds);
        CartDAO cartDAO = new CartDAOImpl(ds);
        orderService = new OrderService(orderDAO, cartDAO);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long userId = currentUserId(req);
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            if ("seller".equals(req.getParameter("as"))) {
                List<OrderDTO> incoming = orderService.sellerIncomingOrders(userId);
                JsonUtil.writeJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(incoming));
            } else {
                List<OrderDTO> history = orderService.buyerHistory(userId);
                JsonUtil.writeJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(history));
            }
            return;
        }

        try {
            long orderId = Long.parseLong(pathInfo.substring(1));
            boolean isAdmin = "ADMIN".equals(req.getSession().getAttribute("role"));
            OrderDTO order = orderService.getOrder(orderId, userId, isAdmin);
            JsonUtil.writeJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(order));
        } catch (NumberFormatException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ApiResponse.fail("VALIDATION_ERROR", "Order id must be numeric"));
        } catch (NotFoundException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_NOT_FOUND,
                    ApiResponse.fail("NOT_FOUND", e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long userId = currentUserId(req);
        try {
            CheckoutRequest body = JsonUtil.fromRequestBody(req, CheckoutRequest.class);
            boolean confirmed = body != null && body.paymentConfirmed;
            OrderDTO order = orderService.placeOrder(userId, confirmed);
            JsonUtil.writeJson(resp, HttpServletResponse.SC_CREATED, ApiResponse.ok(order));
        } catch (ValidationException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ApiResponse.fail("VALIDATION_ERROR", e.getMessage()));
        } catch (DataAccessException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_CONFLICT,
                    ApiResponse.fail("CHECKOUT_CONFLICT", e.getMessage()));
        }
    }

    private long currentUserId(HttpServletRequest req) {
        return (Long) req.getSession().getAttribute("userId");
    }

    /** Body shape for POST /api/v1/orders. */
    private static class CheckoutRequest {
        boolean paymentConfirmed;
    }
}
