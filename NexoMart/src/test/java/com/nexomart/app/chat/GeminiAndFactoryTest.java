package com.nexomart.app.chat;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class GeminiAndFactoryTest {

    @AfterEach
    void clearProperty() {
        System.clearProperty("ai.chatbot.provider");
    }

    // ---- GeminiChatProvider ----

    @Test
    void geminiNameIsGemini() {
        assertEquals("gemini", new GeminiChatProvider("test-key", null).name());
    }

    @Test
    void geminiRejectsMissingKey() {
        assertThrows(IllegalArgumentException.class, () -> new GeminiChatProvider("  ", null));
    }

    @Test
    void extractTextReadsNormalResponse() throws Exception {
        String json = "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"Hello buyer\"}]}}]}";
        assertEquals("Hello buyer", GeminiChatProvider.extractText(json));
    }

    @Test
    void extractTextJoinsMultipleParts() throws Exception {
        String json = "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"Hello \"},{\"text\":\"world\"}]}}]}";
        assertEquals("Hello world", GeminiChatProvider.extractText(json));
    }

    @Test
    void extractTextFailsWhenNoCandidates() {
        assertThrows(ChatProviderException.class,
                () -> GeminiChatProvider.extractText("{\"candidates\":[]}"));
    }

    @Test
    void extractTextFailsWhenTextIsEmpty() {
        String json = "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"  \"}]}}]}";
        assertThrows(ChatProviderException.class, () -> GeminiChatProvider.extractText(json));
    }

    @Test
    void extractTextFailsOnBadJson() {
        assertThrows(ChatProviderException.class, () -> GeminiChatProvider.extractText("not json"));
    }

    // ---- ChatProviderFactory ----

    @Test
    void factoryDefaultsToMock() {
        assumeTrue(System.getenv("AI_CHATBOT_PROVIDER") == null);
        assertEquals("mock", ChatProviderFactory.fromConfig().name());
    }

    @Test
    void factoryUnknownValueFallsBackToMock() {
        assumeTrue(System.getenv("AI_CHATBOT_PROVIDER") == null);
        System.setProperty("ai.chatbot.provider", "banana");
        assertEquals("mock", ChatProviderFactory.fromConfig().name());
    }

    @Test
    void factoryGeminiWithoutKeyFallsBackToMock() {
        assumeTrue(System.getenv("AI_CHATBOT_PROVIDER") == null);
        String key = System.getenv("GEMINI_API_KEY");
        assumeTrue(key == null || key.isBlank());
        System.setProperty("ai.chatbot.provider", "gemini");
        assertEquals("mock", ChatProviderFactory.fromConfig().name());
    }
}