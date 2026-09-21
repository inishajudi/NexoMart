package com.nexomart.app.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.nexomart.app.exception.BusinessRuleException;
import com.nexomart.app.exception.ValidationException;
import com.nexomart.app.filter.AuthFilter;
import com.nexomart.app.model.User;

/**
 * Handles all cart actions under /cart/*:
 *   GET  /cart          - view cart
 *   POST /cart/add      - add item (params: productId, quantity)
 *   POST /cart/update   - update quantity (params: cartItemId, quantity)
 *   POST /cart/remove   - remove item (params: cartItemId)
 */
@WebServlet("/cart/*")
public class CartServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = currentUser(request);
        request.setAttribute("cartItems", cartService.getCart(user.getId()));
        request.setAttribute("cartTotal", cartService.getCartTotal(user.getId()));
        request.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = currentUser(request);
        String path = request.getPathInfo() == null ? "" : request.getPathInfo();

        try {
            switch (path) {
                case "/add" -> {
                    long productId = Long.parseLong(request.getParameter("productId"));
                    int quantity = parseQuantity(request.getParameter("quantity"));
                    cartService.addItem(user.getId(), productId, quantity);
                    request.getSession().setAttribute("flashMessage", "Added to cart.");
                }
                case "/update" -> {
                    long cartItemId = Long.parseLong(request.getParameter("cartItemId"));
                    int quantity = parseQuantity(request.getParameter("quantity"));
                    cartService.updateQuantity(user.getId(), cartItemId, quantity);
                }
                case "/remove" -> {
                    long cartItemId = Long.parseLong(request.getParameter("cartItemId"));
                    cartService.removeItem(user.getId(), cartItemId);
                }
                default -> {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
            }
        } catch (ValidationException | BusinessRuleException e) {
            request.getSession().setAttribute("flashMessage", e.getMessage());
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("flashMessage", "Invalid quantity or item.");
        }

        response.sendRedirect(request.getContextPath() + "/cart");
    }

    private int parseQuantity(String raw) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private User currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return (User) session.getAttribute(AuthFilter.SESSION_USER_ATTR);
    }
}
