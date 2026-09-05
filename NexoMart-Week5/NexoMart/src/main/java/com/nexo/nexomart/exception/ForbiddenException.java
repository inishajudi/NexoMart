package com.nexo.nexomart.exception;

/** Thrown when the requesting user is authenticated but not authorized for this specific
 *  action (e.g. a seller trying to advance the status of an order with none of their
 *  own products in it). Maps to HTTP 403 per Section 13. */
public class ForbiddenException extends Exception {
    public ForbiddenException(String message) {
        super(message);
    }
}
