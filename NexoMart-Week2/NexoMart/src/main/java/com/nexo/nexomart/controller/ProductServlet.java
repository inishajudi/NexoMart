package com.nexo.nexomart.controller;

import com.nexo.nexomart.dao.ProductDAO;
import com.nexo.nexomart.dao.ProductDAOImpl;
import com.nexo.nexomart.dto.ApiResponse;
import com.nexo.nexomart.dto.ProductDTO;
import com.nexo.nexomart.exception.NotFoundException;
import com.nexo.nexomart.exception.ValidationException;
import com.nexo.nexomart.listener.DataSourceProvider;
import com.nexo.nexomart.service.ProductService;
import com.nexo.nexomart.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * GET /api/v1/products              -> browse/search (F3), query params: q, category
 * GET /api/v1/products/{id}         -> single product detail
 * POST /api/v1/products             -> seller creates a listing (F2)
 *
 * Thin by design: no SQL, no business rules here — everything delegates to ProductService.
 */
@WebServlet("/api/v1/products/*")
public class ProductServlet extends HttpServlet {

    private ProductService productService;

    @Override
    public void init() throws ServletException {
        ProductDAO dao = new ProductDAOImpl(DataSourceProvider.get(getServletContext()));
        productService = new ProductService(dao);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                String keyword = req.getParameter("q");
                String category = req.getParameter("category");
                List<ProductDTO> results = productService.browse(keyword, category);
                JsonUtil.writeJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(results));
            } else {
                long id = Long.parseLong(pathInfo.substring(1));
                ProductDTO dto = productService.getById(id);
                JsonUtil.writeJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(dto));
            }
        } catch (NumberFormatException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ApiResponse.fail("VALIDATION_ERROR", "Product id must be numeric"));
        } catch (NotFoundException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_NOT_FOUND,
                    ApiResponse.fail("NOT_FOUND", e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long sellerId = (Long) req.getSession().getAttribute("userId");
        if (sellerId == null) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_UNAUTHORIZED,
                    ApiResponse.fail("UNAUTHENTICATED", "Login required"));
            return;
        }
        try {
            ProductDTO body = JsonUtil.fromRequestBody(req, ProductDTO.class);
            ProductDTO created = productService.create(sellerId, body);
            JsonUtil.writeJson(resp, HttpServletResponse.SC_CREATED, ApiResponse.ok(created));
        } catch (ValidationException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ApiResponse.fail("VALIDATION_ERROR", e.getMessage()));
        }
    }
}
