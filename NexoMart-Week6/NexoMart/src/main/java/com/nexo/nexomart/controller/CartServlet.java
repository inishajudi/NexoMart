package com.nexo.nexomart.controller;

import com.nexo.nexomart.dao.CartDAO;
import com.nexo.nexomart.dao.CartDAOImpl;
import com.nexo.nexomart.dao.ProductDAO;
import com.nexo.nexomart.dao.ProductDAOImpl;
import com.nexo.nexomart.dto.ApiResponse;
import com.nexo.nexomart.dto.CartItemDTO;
import com.nexo.nexomart.dto.CartRequestDTO;
import com.nexo.nexomart.exception.NotFoundException;
import com.nexo.nexomart.exception.ValidationException;
import com.nexo.nexomart.listener.DataSourceProvider;
import com.nexo.nexomart.service.CartService;
import com.nexo.nexomart.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * GET    /api/v1/cart          -> view cart + running total (F4)
 * POST   /api/v1/cart          -> add item, body {productId, quantity}
 * PUT    /api/v1/cart/{id}     -> update quantity, body {quantity}
 * DELETE /api/v1/cart/{id}     -> remove item
 *
 * Sits behind AuthFilter — req.getSession() is guaranteed to hold a userId here.
 */
@WebServlet("/api/v1/cart/*")
public class CartServlet extends HttpServlet {

    private CartService cartService;

    @Override
    public void init() throws ServletException {
        var ds = DataSourceProvider.get(getServletContext());
        CartDAO cartDAO = new CartDAOImpl(ds);
        ProductDAO productDAO = new ProductDAOImpl(ds);
        cartService = new CartService(cartDAO, productDAO);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long userId = currentUserId(req);
        List<CartItemDTO> items = cartService.viewCart(userId);
        BigDecimal total = cartService.runningTotal(userId);
        JsonUtil.writeJson(resp, HttpServletResponse.SC_OK,
                ApiResponse.ok(Map.of("items", items, "total", total)));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long userId = currentUserId(req);
        try {
            CartRequestDTO body = JsonUtil.fromRequestBody(req, CartRequestDTO.class);
            CartItemDTO item = cartService.addItem(userId, body.getProductId(), body.getQuantity());
            JsonUtil.writeJson(resp, HttpServletResponse.SC_CREATED, ApiResponse.ok(item));
        } catch (ValidationException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ApiResponse.fail("VALIDATION_ERROR", e.getMessage()));
        } catch (NotFoundException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_NOT_FOUND,
                    ApiResponse.fail("NOT_FOUND", e.getMessage()));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long userId = currentUserId(req);
        Long cartItemId = pathId(req);
        if (cartItemId == null) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ApiResponse.fail("VALIDATION_ERROR", "Cart item id required in path"));
            return;
        }
        try {
            CartRequestDTO body = JsonUtil.fromRequestBody(req, CartRequestDTO.class);
            CartItemDTO updated = cartService.updateQuantity(userId, cartItemId, body.getQuantity());
            JsonUtil.writeJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(updated));
        } catch (ValidationException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ApiResponse.fail("VALIDATION_ERROR", e.getMessage()));
        } catch (NotFoundException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_NOT_FOUND,
                    ApiResponse.fail("NOT_FOUND", e.getMessage()));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long userId = currentUserId(req);
        Long cartItemId = pathId(req);
        if (cartItemId == null) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ApiResponse.fail("VALIDATION_ERROR", "Cart item id required in path"));
            return;
        }
        try {
            cartService.removeItem(userId, cartItemId);
            JsonUtil.writeJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(Map.of("deleted", true)));
        } catch (NotFoundException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_NOT_FOUND,
                    ApiResponse.fail("NOT_FOUND", e.getMessage()));
        }
    }

    private long currentUserId(HttpServletRequest req) {
        return (Long) req.getSession().getAttribute("userId");
    }

    private Long pathId(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            return null;
        }
        try {
            return Long.parseLong(pathInfo.substring(1));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
