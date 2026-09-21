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
import com.nexomart.app.exception.ValidationException;
import com.nexomart.app.filter.AuthFilter;
import com.nexomart.app.model.User;

@WebServlet("/products/delete")
public class ProductDeleteServlet extends BaseServlet {

    private static final Logger log = LoggerFactory.getLogger(ProductDeleteServlet.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User seller = (User) session.getAttribute(AuthFilter.SESSION_USER_ATTR);

        long productId;
        try {
            productId = Long.parseLong(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/products");
            return;
        }

        try {
            productService.deleteListing(seller.getId(), productId);
            log.info("Seller {} deleted product id {}", seller.getEmail(), productId);
            request.getSession().setAttribute("flashMessage", "Listing deleted successfully.");
        } catch (ValidationException | BusinessRuleException e) {
            request.getSession().setAttribute("flashMessage", "Error: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/products");
    }
}