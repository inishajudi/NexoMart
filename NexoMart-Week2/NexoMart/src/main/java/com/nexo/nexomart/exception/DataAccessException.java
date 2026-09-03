package com.nexo.nexomart.exception;

/** Wraps checked SQLExceptions thrown from the DAO layer into an unchecked exception
 *  so service methods don't have to declare SQLException everywhere. */
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
