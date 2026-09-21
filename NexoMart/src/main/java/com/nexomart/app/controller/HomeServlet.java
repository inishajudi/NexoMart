package com.nexomart.app.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nexomart.app.model.Product;

@WebServlet(urlPatterns = {""})
public class HomeServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Product> featuredProducts = new ArrayList<>(productService.findAll());
        featuredProducts.sort(
                Comparator.comparing(Product::getCategory)
                        .thenComparing(Product::getId, Comparator.reverseOrder()));

        request.setAttribute("featuredProducts", featuredProducts);
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}