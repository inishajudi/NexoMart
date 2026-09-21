package com.nexomart.app.exception;

/** Thrown when an operation violates a business rule (e.g. insufficient stock, empty cart). */
public class BusinessRuleException extends Exception {
    public BusinessRuleException(String message) {
        super(message);
    }
}
