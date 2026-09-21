package com.nexomart.app.controller;

import java.io.IOException;
import java.math.BigDecimal;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nexomart.app.dto.ProductRequest;
import com.nexomart.app.exception.BusinessRuleException;
import com.nexomart.app.exception.ValidationException;
import com.nexomart.app.filter.AuthFilter;
import com.nexomart.app.model.Product;
import com.nexomart.app.model.User;

@WebServlet("/products/edit")
public class ProductEditServlet extends BaseServlet {

    private static final Logger log = LoggerFactory.getLogger(ProductEditServlet.class);

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

        productService.findById(productId).ifPresentOrElse(
                product -> {
                    request.setAttribute("product", product);
                    try {
                        request.getRequestDispatcher("/WEB-INF/views/edit-product.jsp").forward(request, response);
                    } catch (ServletException | IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                () -> {
                    try {
                        response.sendRedirect(request.getContextPath() + "/products");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

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

        ProductRequest productRequest = new ProductRequest();
        productRequest.setName(request.getParameter("name"));
        productRequest.setDescription(request.getParameter("description"));
        productRequest.setCategory(request.getParameter("category"));
        productRequest.setImageUrl(request.getParameter("imageUrl"));
        try {
            productRequest.setPrice(new BigDecimal(request.getParameter("price")));
            productRequest.setStockQty(Integer.parseInt(request.getParameter("stockQty")));
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Price and stock quantity must be valid numbers.");
            request.getRequestDispatcher("/WEB-INF/views/edit-product.jsp").forward(request, response);
            return;
        }

        try {
            Product updated = productService.updateListing(seller.getId(), productId, productRequest);
            log.info("Seller {} updated product '{}'", seller.getEmail(), updated.getName());
            request.getSession().setAttribute("flashMessage", "Listing updated: " + updated.getName());
            response.sendRedirect(request.getContextPath() + "/products");
        } catch (ValidationException | BusinessRuleException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/edit-product.jsp").forward(request, response);
        }
    }
}