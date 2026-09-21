package com.nexomart.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nexomart.app.dao.OrderDao;
import com.nexomart.app.dao.ReviewDao;
import com.nexomart.app.exception.BusinessRuleException;
import com.nexomart.app.exception.ValidationException;
import com.nexomart.app.model.Review;

class ReviewServiceTest {

    private ReviewDao reviewDao;
    private OrderDao orderDao;
    private ReviewService service;

    @BeforeEach
    void setUp() {
        reviewDao = mock(ReviewDao.class);
        orderDao = mock(OrderDao.class);
        service = new ReviewService(reviewDao, orderDao);
    }

    @Test
    void ratingAboveFiveIsRejected() {
        assertThrows(ValidationException.class, () -> service.submitReview(1L, 2L, 6, "Great"));
        verify(reviewDao, never()).insert(any(Review.class));
    }

    @Test
    void reviewWithoutCompletedOrderIsRejected() {
        when(orderDao.hasDeliveredOrderForProduct(1L, 2L)).thenReturn(false);
        assertThrows(BusinessRuleException.class, () -> service.submitReview(1L, 2L, 5, "Great"));
        verify(reviewDao, never()).insert(any(Review.class));
    }

    @Test
    void duplicateReviewIsRejected() {
        when(orderDao.hasDeliveredOrderForProduct(1L, 2L)).thenReturn(true);
        when(reviewDao.existsByUserAndProduct(1L, 2L)).thenReturn(true);
        assertThrows(BusinessRuleException.class, () -> service.submitReview(1L, 2L, 5, "Great"));
        verify(reviewDao, never()).insert(any(Review.class));
    }

    @Test
    void validReviewIsSavedWithTrimmedComment() throws Exception {
        when(orderDao.hasDeliveredOrderForProduct(1L, 2L)).thenReturn(true);
        when(reviewDao.existsByUserAndProduct(1L, 2L)).thenReturn(false);
        when(reviewDao.insert(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));

        Review saved = service.submitReview(1L, 2L, 4, "  Nice product  ");

        assertEquals(4, saved.getRating());
        assertEquals("Nice product", saved.getComment());
    }
}