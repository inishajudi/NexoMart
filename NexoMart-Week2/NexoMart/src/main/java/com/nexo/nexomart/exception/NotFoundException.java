package com.nexo.nexomart.exception;

/** Thrown when a requested entity (product, cart item, order) does not exist. */
public class NotFoundException extends Exception {
    public NotFoundException(String message) {
        super(message);
    }
}
