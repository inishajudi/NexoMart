package com.nexo.nexomart.controller;

import com.nexo.nexomart.dao.ProductDAO;
import com.nexo.nexomart.dao.ProductDAOImpl;
import com.nexo.nexomart.dao.ReviewDAO;
import com.nexo.nexomart.dao.ReviewDAOImpl;
import com.nexo.nexomart.dto.ApiResponse;
import com.nexo.nexomart.dto.ProductDTO;
import com.nexo.nexomart.dto.ProductSearchCriteria;
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
 * GET    /api/v1/products              -> browse/search (F3), query params: q, category,
 *                                          minPrice, maxPrice, sort (Week 5 additions)
 * GET    /api/v1/products/mine         -> seller's own listings, for the dashboard (Week 3)
 * GET    /api/v1/products/{id}         -> single product detail
 * POST   /api/v1/products              -> seller creates a listing (F2)
 * PUT    /api/v1/products/{id}         -> seller edits their own listing (F2, Week 3)
 * DELETE /api/v1/products/{id}         -> seller deletes their own listing (F2, Week 3)
 *
 * Thin by design: no SQL, no business rules here — everything delegates to ProductService.
 * Write operations (POST/PUT/DELETE) require a session; ProductService enforces that a
 * seller can only edit/delete listings they own.
 */
@WebServlet("/api/v1/products/*")
public class ProductServlet extends HttpServlet {

    private ProductService productService;
    private ReviewDAO reviewDAO;

    @Override
    public void init() throws ServletException {
        ProductDAO dao = new ProductDAOImpl(DataSourceProvider.get(getServletContext()));
        productService = new ProductService(dao);
        reviewDAO = new ReviewDAOImpl(DataSourceProvider.get(getServletContext()));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                ProductSearchCriteria criteria = ProductSearchCriteria.empty()
                        .keyword(req.getParameter("q"))
                        .category(req.getParameter("category"))
                        .minPrice(parseBigDecimal(req.getParameter("minPrice")))
                        .maxPrice(parseBigDecimal(req.getParameter("maxPrice")))
                        .sortBy(parseSort(req.getParameter("sort")));
                List<ProductDTO> results = productService.browse(criteria);
                JsonUtil.writeJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(results));
            } else if (pathInfo.equals("/mine")) {
                Long sellerId = requireSeller(req, resp);
                if (sellerId == null) return;
                List<ProductDTO> mine = productService.sellerListings(sellerId);
                JsonUtil.writeJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(mine));
            } else {
                long id = Long.parseLong(pathInfo.substring(1));
                ProductDTO dto = productService.getById(id);
                var rating = reviewDAO.getRatingSummary(id);
                JsonUtil.writeJson(resp, HttpServletResponse.SC_OK,
                        ApiResponse.ok(java.util.Map.of("product", dto, "rating", rating)));
            }
        } catch (NumberFormatException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ApiResponse.fail("VALIDATION_ERROR", "Product id must be numeric"));
        } catch (NotFoundException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_NOT_FOUND,
                    ApiResponse.fail("NOT_FOUND", e.getMessage()));
        } catch (ValidationException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ApiResponse.fail("VALIDATION_ERROR", e.getMessage()));
        }
    }

    private java.math.BigDecimal parseBigDecimal(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try {
            return new java.math.BigDecimal(raw);
        } catch (NumberFormatException e) {
            return null; // treat a malformed price filter as "not provided" rather than 400ing browse
        }
    }

    private ProductSearchCriteria.SortBy parseSort(String raw) {
        if (raw == null || raw.isBlank()) return ProductSearchCriteria.SortBy.NEWEST;
        try {
            return ProductSearchCriteria.SortBy.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ProductSearchCriteria.SortBy.NEWEST;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long sellerId = requireSeller(req, resp);
        if (sellerId == null) return;
        try {
            ProductDTO body = JsonUtil.fromRequestBody(req, ProductDTO.class);
            ProductDTO created = productService.create(sellerId, body);
            JsonUtil.writeJson(resp, HttpServletResponse.SC_CREATED, ApiResponse.ok(created));
        } catch (ValidationException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ApiResponse.fail("VALIDATION_ERROR", e.getMessage()));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long sellerId = requireSeller(req, resp);
        if (sellerId == null) return;

        Long productId = pathId(req);
        if (productId == null) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ApiResponse.fail("VALIDATION_ERROR", "Product id required in path"));
            return;
        }
        try {
            ProductDTO body = JsonUtil.fromRequestBody(req, ProductDTO.class);
            ProductDTO updated = productService.update(sellerId, productId, body);
            JsonUtil.writeJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(updated));
        } catch (ValidationException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ApiResponse.fail("VALIDATION_ERROR", e.getMessage()));
        } catch (NotFoundException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_NOT_FOUND,
                    ApiResponse.fail("NOT_FOUND", e.getMessage()));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long sellerId = requireSeller(req, resp);
        if (sellerId == null) return;

        Long productId = pathId(req);
        if (productId == null) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ApiResponse.fail("VALIDATION_ERROR", "Product id required in path"));
            return;
        }
        try {
            productService.delete(sellerId, productId);
            JsonUtil.writeJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(java.util.Map.of("deleted", true)));
        } catch (NotFoundException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_NOT_FOUND,
                    ApiResponse.fail("NOT_FOUND", e.getMessage()));
        }
    }

    /** Returns the session's userId, or writes a 401 and returns null if there isn't one.
     *  Named "requireSeller" for readability at call sites; role-level (SELLER vs BUYER)
     *  authorization is intentionally left to Week 1's auth work / a future AuthFilter
     *  extension — this only enforces "is logged in". */
    private Long requireSeller(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long userId = (Long) req.getSession().getAttribute("userId");
        if (userId == null) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_UNAUTHORIZED,
                    ApiResponse.fail("UNAUTHENTICATED", "Login required"));
            return null;
        }
        return userId;
    }

    private Long pathId(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            return null;
        }
        try {
            return Long.parseLong(pathInfo.substring(1));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
