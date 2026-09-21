package com.nexomart.app.exception;

/** Thrown when login credentials are invalid or a session is unauthorized. */
public class AuthenticationException extends Exception {
    public AuthenticationException(String message) {
        super(message);
    }
}
