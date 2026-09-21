package com.nexomart.app.exception;

/** Thrown when user-supplied input fails validation rules. */
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}
