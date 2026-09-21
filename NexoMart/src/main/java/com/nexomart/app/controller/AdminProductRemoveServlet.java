package com.nexomart.app.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nexomart.app.exception.BusinessRuleException;
import com.nexomart.app.exception.ValidationException;

@WebServlet("/admin/products/remove")
public class AdminProductRemoveServlet extends BaseServlet {

    private static final Logger log = LoggerFactory.getLogger(AdminProductRemoveServlet.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        long productId;
        try {
            productId = Long.parseLong(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }

        try {
            productService.adminRemoveListing(productId);
            log.info("Admin removed product id {}", productId);
            request.getSession().setAttribute("flashMessage", "Listing removed by admin.");
        } catch (ValidationException | BusinessRuleException e) {
            request.getSession().setAttribute("flashMessage", "Error: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
    }
}