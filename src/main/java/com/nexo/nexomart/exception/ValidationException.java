package com.nexo.nexomart.exception;

/**
 * Thrown by the service layer when input fails validation before any DAO call
 * is made (spec Section 13 rule 5). Callers should map this to HTTP 400.
 */
public class ValidationException extends Exception {

    private final String field;

    public ValidationException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
