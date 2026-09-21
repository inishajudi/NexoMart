package com.nexomart.app.chat;

/** Thrown when a ChatProvider cannot produce a reply. */
public class ChatProviderException extends Exception {

    private static final long serialVersionUID = 1L;

    public ChatProviderException(String message) {
        super(message);
    }

    public ChatProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
