package com.nexomart.app.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.nexomart.app.filter.AuthFilter;
import com.nexomart.app.model.Product;
import com.nexomart.app.model.User;

@WebServlet("/wishlist")
public class WishlistServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AuthFilter.SESSION_USER_ATTR);
        List<Product> items = wishlistDao.findByUser(user.getId());
        request.setAttribute("items", items);
        request.getRequestDispatcher("/WEB-INF/views/wishlist.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AuthFilter.SESSION_USER_ATTR);
        if (user.getRole() != User.Role.BUYER) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        long productId = Long.parseLong(request.getParameter("productId"));
        if ("remove".equals(request.getParameter("action"))) {
            wishlistDao.remove(user.getId(), productId);
        } else {
            wishlistDao.add(user.getId(), productId);
        }
        response.sendRedirect(request.getContextPath() + "/wishlist");
    }
}