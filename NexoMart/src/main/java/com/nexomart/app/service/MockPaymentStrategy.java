package com.nexomart.app.service;

import java.math.BigDecimal;

/**
 * Mock payment strategy — always approves.
 * Satisfies the Strategy design-pattern requirement (Section 12)
 * and the mock-payment requirement (F5).
 */
public class MockPaymentStrategy implements PaymentStrategy {

    @Override
    public boolean process(BigDecimal amount) {
        // No real gateway. Always returns true (mock confirmation).
        return true;
    }
}
