package com.nexomart.app.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.nexomart.app.filter.AuthFilter;
import com.nexomart.app.model.Order;
import com.nexomart.app.model.User;

@WebServlet("/orders/seller")
public class SellerOrdersServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User seller = (User) session.getAttribute(AuthFilter.SESSION_USER_ATTR);

        if (seller.getRole() != User.Role.SELLER) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only sellers can view this page.");
            return;
        }

        List<Order> orders = orderService.getSellerOrders(seller.getId());
        request.setAttribute("orders", orders);
        request.getRequestDispatcher("/WEB-INF/views/seller-orders.jsp").forward(request, response);
    }
@Override
protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    HttpSession session = request.getSession(false);
    User seller = (User) session.getAttribute(AuthFilter.SESSION_USER_ATTR);
    if (seller.getRole() != User.Role.SELLER) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return;
    }

    long orderId = Long.parseLong(request.getParameter("orderId"));
    String action = request.getParameter("action");

    if ("ship".equals(action)) {
        orderService.markShipped(orderId);
    } else if ("deliver".equals(action)) {
        orderService.markDelivered(orderId);
    }

    response.sendRedirect(request.getContextPath() + "/orders/seller");
}
}