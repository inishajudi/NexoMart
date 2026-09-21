package com.nexomart.app.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nexomart.app.model.Product;

@WebServlet("/products/view")
public class ProductDetailServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        long productId;
        try {
            productId = Long.parseLong(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/products");
            return;
        }

        Product product = productService.findById(productId).orElse(null);
        if (product == null) {
            response.sendRedirect(request.getContextPath() + "/products");
            return;
        }

        request.setAttribute("product", product);
        request.setAttribute("reviews", reviewService.getReviewsForProduct(productId));
        request.setAttribute("averageRating", reviewService.getAverageRating(productId));
        request.getRequestDispatcher("/WEB-INF/views/product-detail.jsp").forward(request, response);
    }
}