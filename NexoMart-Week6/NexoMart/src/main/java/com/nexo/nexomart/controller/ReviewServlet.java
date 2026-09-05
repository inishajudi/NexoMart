package com.nexo.nexomart.controller;

import com.nexo.nexomart.dao.ProductDAO;
import com.nexo.nexomart.dao.ProductDAOImpl;
import com.nexo.nexomart.dao.ReviewDAO;
import com.nexo.nexomart.dao.ReviewDAOImpl;
import com.nexo.nexomart.dto.ApiResponse;
import com.nexo.nexomart.dto.ReviewDTO;
import com.nexo.nexomart.dto.ReviewRequestDTO;
import com.nexo.nexomart.exception.NotFoundException;
import com.nexo.nexomart.exception.ValidationException;
import com.nexo.nexomart.listener.DataSourceProvider;
import com.nexo.nexomart.service.ReviewService;
import com.nexo.nexomart.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * GET  /api/v1/reviews?productId={id}   -> public, list reviews for a product (F8)
 * POST /api/v1/reviews                  -> submit a review, body {productId, rating, comment}
 *                                          (F8; requires a DELIVERED order for that product)
 *
 * Not behind AuthFilter (GET must be public); POST checks the session itself, same
 * pattern as ProductServlet's write endpoints.
 */
@WebServlet("/api/v1/reviews/*")
public class ReviewServlet extends HttpServlet {

    private ReviewService reviewService;

    @Override
    public void init() throws ServletException {
        var ds = DataSourceProvider.get(getServletContext());
        ReviewDAO reviewDAO = new ReviewDAOImpl(ds);
        ProductDAO productDAO = new ProductDAOImpl(ds);
        reviewService = new ReviewService(reviewDAO, productDAO);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String productIdRaw = req.getParameter("productId");
        if (productIdRaw == null) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ApiResponse.fail("VALIDATION_ERROR", "productId query param is required"));
            return;
        }
        try {
            long productId = Long.parseLong(productIdRaw);
            List<ReviewDTO> reviews = reviewService.listForProduct(productId);
            var summary = reviewService.ratingSummary(productId);
            JsonUtil.writeJson(resp, HttpServletResponse.SC_OK,
                    ApiResponse.ok(java.util.Map.of("reviews", reviews, "summary", summary)));
        } catch (NumberFormatException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ApiResponse.fail("VALIDATION_ERROR", "productId must be numeric"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long userId = (Long) req.getSession().getAttribute("userId");
        if (userId == null) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_UNAUTHORIZED,
                    ApiResponse.fail("UNAUTHENTICATED", "Login required"));
            return;
        }
        try {
            ReviewRequestDTO body = JsonUtil.fromRequestBody(req, ReviewRequestDTO.class);
            ReviewDTO created = reviewService.submitReview(
                    userId, body.getProductId(), body.getRating(), body.getComment());
            JsonUtil.writeJson(resp, HttpServletResponse.SC_CREATED, ApiResponse.ok(created));
        } catch (ValidationException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ApiResponse.fail("VALIDATION_ERROR", e.getMessage()));
        } catch (NotFoundException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_NOT_FOUND,
                    ApiResponse.fail("NOT_FOUND", e.getMessage()));
        }
    }
}
