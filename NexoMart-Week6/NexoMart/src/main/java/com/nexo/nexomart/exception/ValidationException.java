package com.nexo.nexomart.exception;

/** Thrown when incoming request data fails service-layer validation. */
public class ValidationException extends Exception {
    private final String field;

    public ValidationException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() { return field; }
}
