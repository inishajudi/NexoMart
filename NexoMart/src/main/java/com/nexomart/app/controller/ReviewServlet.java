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

/**
 * Handles review submission under /reviews/add (params: productId, rating, comment).
 * On success or failure, redirects back to the product detail page.
 */
@WebServlet("/reviews/add")
public class ReviewServlet extends BaseServlet {

    private static final Logger log = LoggerFactory.getLogger(ReviewServlet.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute(AuthFilter.SESSION_USER_ATTR);

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirectReason=auth");
            return;
        }

        long productId;
        int rating;
        try {
            productId = Long.parseLong(request.getParameter("productId"));
            rating = Integer.parseInt(request.getParameter("rating"));
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("flashMessage", "Invalid review submission.");
            response.sendRedirect(request.getContextPath() + "/products");
            return;
        }
        String comment = request.getParameter("comment");

        try {
            reviewService.submitReview(user.getId(), productId, rating, comment);
            log.info("User {} submitted a review for product {}", user.getEmail(), productId);
            request.getSession().setAttribute("flashMessage", "Review submitted. Thank you!");
        } catch (ValidationException | BusinessRuleException e) {
            request.getSession().setAttribute("flashMessage", "Error: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/products/view?id=" + productId);
    }
}