package com.nexomart.app.chat;

/**
 * Abstraction over whatever produces chatbot replies (real LLM or mock).
 * Implementations must be thread-safe: one instance is shared by all requests.
 */
public interface ChatProvider {

    /** Short identifier, e.g. "mock" or "gemini". Used for logging. */
    String name();

    /**
     * Produce a reply for a single user message.
     *
     * @throws ChatProviderException if the reply cannot be produced
     *         (timeout, bad response, network error). Callers must catch this
     *         and return a degraded response instead of an error page.
     */
    String reply(String userMessage) throws ChatProviderException;
}
