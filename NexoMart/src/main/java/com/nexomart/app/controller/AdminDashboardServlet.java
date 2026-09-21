package com.nexomart.app.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nexomart.app.model.Order;
import com.nexomart.app.model.Product;
import com.nexomart.app.model.User;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<User> users = userService.findAll();
        List<Product> products = productService.findAll();
        List<Order> orders = orderService.findAllOrders();

        Map<Long, String> buyerNames = new HashMap<>();
        for (User u : users) {
            buyerNames.put(u.getId(), u.getName());
        }

        request.setAttribute("users", users);
        request.setAttribute("products", products);
        request.setAttribute("orders", orders);
        request.setAttribute("buyerNames", buyerNames);
        request.getRequestDispatcher("/WEB-INF/views/admin-dashboard.jsp").forward(request, response);
    }
}