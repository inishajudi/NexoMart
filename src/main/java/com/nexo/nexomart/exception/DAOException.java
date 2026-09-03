package com.nexo.nexomart.exception;

/**
 * Wraps low-level SQLException so the DAO interface contract doesn't leak
 * java.sql details into the service layer.
 */
public class DAOException extends RuntimeException {

    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }
}
