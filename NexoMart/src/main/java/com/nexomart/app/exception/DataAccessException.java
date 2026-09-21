package com.nexomart.app.exception;

/** Wraps low-level SQLExceptions so upper layers don't depend on java.sql directly. */
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
