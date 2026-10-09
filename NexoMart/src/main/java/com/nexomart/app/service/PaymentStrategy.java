package com.nexomart.app.service;

import java.math.BigDecimal;

/**
 * Strategy interface for payment processing.
 * Allows swapping real gateway implementations without changing OrderService.
 */
public interface PaymentStrategy {

    /**
     * Processes payment for the given amount.
     *
     * @param amount the total to charge
     * @return true if payment succeeded
     */
    boolean process(BigDecimal amount);
}
