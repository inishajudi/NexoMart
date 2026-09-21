package com.nexomart.app.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.nexomart.app.filter.AuthFilter;
import com.nexomart.app.model.User;

@WebServlet("/orders")
public class OrderHistoryServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AuthFilter.SESSION_USER_ATTR);

        request.setAttribute("orders", orderService.getOrderHistory(user.getId()));
        request.getRequestDispatcher("/WEB-INF/views/orders.jsp").forward(request, response);
    }
}
