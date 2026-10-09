package com.nexomart.app.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.nexomart.app.dto.SellerDashboardDTO;
import com.nexomart.app.filter.AuthFilter;
import com.nexomart.app.model.User;

/**
 * Serves the seller sales dashboard at /seller/dashboard.
 */
@WebServlet("/seller/dashboard")
public class SellerDashboardServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User seller = (User) session.getAttribute(AuthFilter.SESSION_USER_ATTR);

        if (seller.getRole() != User.Role.SELLER) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only sellers can view this page.");
            return;
        }

        SellerDashboardDTO dashboard = orderService.getSellerDashboard(seller.getId());
        request.setAttribute("dashboard", dashboard);
        request.getRequestDispatcher("/WEB-INF/views/seller-dashboard.jsp").forward(request, response);
    }
}
