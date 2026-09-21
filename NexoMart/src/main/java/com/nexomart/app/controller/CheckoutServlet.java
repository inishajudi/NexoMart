package com.nexomart.app.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nexomart.app.exception.BusinessRuleException;
import com.nexomart.app.filter.AuthFilter;
import com.nexomart.app.model.Order;
import com.nexomart.app.model.User;

/**
 * GET  /checkout  - review cart contents and total before confirming
 * POST /checkout  - "Confirm Payment" button; mock-confirms and places the order
 */
@WebServlet("/checkout")
public class CheckoutServlet extends BaseServlet {

    private static final Logger log = LoggerFactory.getLogger(CheckoutServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = currentUser(request);
        request.setAttribute("cartItems", cartService.getCart(user.getId()));
        request.setAttribute("cartTotal", cartService.getCartTotal(user.getId()));
        request.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = currentUser(request);
        try {
            Order order = orderService.checkout(user.getId());
            log.info("Order {} confirmed for buyer {}", order.getId(), user.getEmail());
            request.getSession().setAttribute("flashMessage",
                    "Payment confirmed! Order #" + order.getId() + " placed.");
            response.sendRedirect(request.getContextPath() + "/orders");
        } catch (BusinessRuleException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("cartItems", cartService.getCart(user.getId()));
            request.setAttribute("cartTotal", cartService.getCartTotal(user.getId()));
            request.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(request, response);
        }
    }

    private User currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return (User) session.getAttribute(AuthFilter.SESSION_USER_ATTR);
    }
}
