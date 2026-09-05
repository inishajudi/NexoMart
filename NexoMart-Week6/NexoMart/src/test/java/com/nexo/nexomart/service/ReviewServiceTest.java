package com.nexo.nexomart.service;

import com.nexo.nexomart.dao.ProductDAO;
import com.nexo.nexomart.dao.ReviewDAO;
import com.nexo.nexomart.dto.RatingSummary;
import com.nexo.nexomart.exception.NotFoundException;
import com.nexo.nexomart.exception.ValidationException;
import com.nexo.nexomart.model.Product;
import com.nexo.nexomart.model.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Covers F8's eligibility rules: DAOs mocked, per Section 9. */
class ReviewServiceTest {

    private ReviewDAO reviewDAO;
    private ProductDAO productDAO;
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        reviewDAO = mock(ReviewDAO.class);
        productDAO = mock(ProductDAO.class);
        reviewService = new ReviewService(reviewDAO, productDAO);

        Product p = new Product();
        p.setId(1L);
        p.setSellerId(2L);
        p.setName("Test product");
        p.setPrice(new BigDecimal("10.00"));
        p.setStockQty(5);
        lenientFindById(p);
    }

    private void lenientFindById(Product p) {
        when(productDAO.findById(1L)).thenReturn(Optional.of(p));
    }

    @Test
    void submitReview_succeedsForDeliveredBuyerWithNoExistingReview() throws Exception {
        when(reviewDAO.hasDeliveredOrderForProduct(3L, 1L)).thenReturn(true);
        when(reviewDAO.existsForUserAndProduct(3L, 1L)).thenReturn(false);
        when(reviewDAO.create(any())).thenAnswer(inv -> {
            Review r = inv.getArgument(0);
            r.setId(100L);
            return r;
        });

        var dto = reviewService.submitReview(3L, 1L, 5, "Great product");

        assertEquals(5, dto.getRating());
        verify(reviewDAO).create(any());
    }

    @Test
    void submitReview_rejectsWithoutDeliveredOrder() {
        when(reviewDAO.hasDeliveredOrderForProduct(3L, 1L)).thenReturn(false);

        assertThrows(ValidationException.class, () -> reviewService.submitReview(3L, 1L, 5, "Nice"));
        verify(reviewDAO, never()).create(any());
    }

    @Test
    void submitReview_rejectsDuplicateReview() {
        when(reviewDAO.hasDeliveredOrderForProduct(3L, 1L)).thenReturn(true);
        when(reviewDAO.existsForUserAndProduct(3L, 1L)).thenReturn(true);

        assertThrows(ValidationException.class, () -> reviewService.submitReview(3L, 1L, 4, "Again"));
        verify(reviewDAO, never()).create(any());
    }

    @Test
    void submitReview_rejectsOutOfRangeRating() {
        assertThrows(ValidationException.class, () -> reviewService.submitReview(3L, 1L, 6, "Too high"));
        assertThrows(ValidationException.class, () -> reviewService.submitReview(3L, 1L, 0, "Too low"));
    }

    @Test
    void submitReview_rejectsUnknownProduct() {
        when(productDAO.findById(999L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> reviewService.submitReview(3L, 999L, 5, "x"));
    }

    @Test
    void ratingSummary_delegatesToDAO() {
        when(reviewDAO.getRatingSummary(1L)).thenReturn(new RatingSummary(4.5, 2));

        var summary = reviewService.ratingSummary(1L);

        assertEquals(4.5, summary.getAverageRating());
        assertEquals(2, summary.getReviewCount());
    }
}
