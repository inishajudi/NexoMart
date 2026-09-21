package com.nexomart.app.chat;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Picks the ChatProvider from configuration.
 *
 * Provider (first non-blank wins):
 *   1. environment variable  AI_CHATBOT_PROVIDER   (use this on Render)
 *   2. JVM system property   ai.chatbot.provider   (use this locally: -Dai.chatbot.provider=mock)
 *   3. default               mock
 *
 * Gemini settings (environment variables only, never in git):
 *   GEMINI_API_KEY   required for gemini; if missing we fall back to mock
 *   GEMINI_MODEL     optional; default gemini-2.5-flash
 */
public final class ChatProviderFactory {

    private static final Logger log = LoggerFactory.getLogger(ChatProviderFactory.class);

    private ChatProviderFactory() { }

    public static ChatProvider fromConfig() {
        String choice = read("AI_CHATBOT_PROVIDER", "ai.chatbot.provider", "mock")
                .trim().toLowerCase(Locale.ROOT);

        switch (choice) {
            case "gemini":
                String key = System.getenv("GEMINI_API_KEY");
                if (key == null || key.isBlank()) {
                    log.warn("ai.chatbot.provider=gemini but GEMINI_API_KEY is not set; using mock");
                    return new MockChatProvider();
                }
                String model = System.getenv("GEMINI_MODEL");
                return new GeminiChatProvider(key, model);
            case "mock":
                return new MockChatProvider();
            default:
                log.warn("Unknown ai.chatbot.provider '{}'; using mock", choice);
                return new MockChatProvider();
        }
    }

    private static String read(String envName, String propName, String defaultValue) {
        String value = System.getenv(envName);
        if (value == null || value.isBlank()) {
            value = System.getProperty(propName);
        }
        return (value == null || value.isBlank()) ? defaultValue : value;
    }
}