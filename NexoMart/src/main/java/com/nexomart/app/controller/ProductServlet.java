package com.nexomart.app.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nexomart.app.model.Product;

@WebServlet("/products")
public class ProductServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        String category = request.getParameter("category");

        List<Product> products = (keyword != null && !keyword.isBlank())
                || (category != null && !category.isBlank())
                ? productService.search(keyword, category)
                : productService.findAll();

        request.setAttribute("products", products);
        request.setAttribute("keyword", keyword);
        request.setAttribute("category", category);
        request.getRequestDispatcher("/WEB-INF/views/products.jsp").forward(request, response);
    }
}
