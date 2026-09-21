package com.nexomart.app.chat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Real provider: calls the Gemini REST API from the server.
 * The API key is only sent in a request header and is never logged.
 */
public class GeminiChatProvider implements ChatProvider {

    static final String DEFAULT_MODEL = "gemini-2.5-flash";
    private static final String DEFAULT_BASE =
            "https://generativelanguage.googleapis.com/v1beta/models/";

    private final String apiKey;
    private final String endpoint;
    private final Duration requestTimeout;
    private final HttpClient client;

    public GeminiChatProvider(String apiKey, String model) {
        this(apiKey, model, DEFAULT_BASE, Duration.ofSeconds(8));
    }

    /** Package-private: tests can use another base URL and timeout. */
    GeminiChatProvider(String apiKey, String model, String baseUrl, Duration requestTimeout) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("Gemini API key is missing");
        }
        String m = (model == null || model.isBlank()) ? DEFAULT_MODEL : model.trim();
        this.apiKey = apiKey.trim();
        this.endpoint = baseUrl + m + ":generateContent";
        this.requestTimeout = requestTimeout;
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    @Override
    public String name() {
        return "gemini";
    }

    @Override
    public String reply(String userMessage) throws ChatProviderException {
        JsonObject body = buildBody(userMessage);

        HttpRequest request = HttpRequest.newBuilder(URI.create(endpoint))
                .timeout(requestTimeout)
                .header("Content-Type", "application/json")
                .header("x-goog-api-key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ChatProviderException("Gemini call interrupted", e);
        } catch (IOException e) {
            throw new ChatProviderException("Gemini call failed: " + e.getClass().getSimpleName(), e);
        }

        if (response.statusCode() != 200) {
            throw new ChatProviderException("Gemini returned HTTP " + response.statusCode());
        }
        return extractText(response.body());
    }

    private static JsonObject buildBody(String userMessage) {
        JsonObject sysPart = new JsonObject();
        sysPart.addProperty("text", ChatPrompt.SYSTEM);
        JsonArray sysParts = new JsonArray();
        sysParts.add(sysPart);
        JsonObject systemInstruction = new JsonObject();
        systemInstruction.add("parts", sysParts);

        JsonObject userPart = new JsonObject();
        userPart.addProperty("text", userMessage);
        JsonArray userParts = new JsonArray();
        userParts.add(userPart);
        JsonObject content = new JsonObject();
        content.addProperty("role", "user");
        content.add("parts", userParts);
        JsonArray contents = new JsonArray();
        contents.add(content);

        JsonObject config = new JsonObject();
        config.addProperty("maxOutputTokens", 1024);
        config.addProperty("temperature", 0.3);

        JsonObject body = new JsonObject();
        body.add("system_instruction", systemInstruction);
        body.add("contents", contents);
        body.add("generationConfig", config);
        return body;
    }

    static String extractText(String json) throws ChatProviderException {
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonArray candidates = root.getAsJsonArray("candidates");
            if (candidates == null || candidates.size() == 0) {
                throw new ChatProviderException("Gemini returned no candidates");
            }
            JsonObject content = candidates.get(0).getAsJsonObject().getAsJsonObject("content");
            if (content == null) {
                throw new ChatProviderException("Gemini candidate has no content");
            }
            JsonArray parts = content.getAsJsonArray("parts");
            if (parts == null || parts.size() == 0) {
                throw new ChatProviderException("Gemini content has no parts");
            }
            StringBuilder sb = new StringBuilder();
            for (JsonElement p : parts) {
                JsonElement t = p.getAsJsonObject().get("text");
                if (t != null && !t.isJsonNull()) {
                    sb.append(t.getAsString());
                }
            }
            String text = sb.toString().trim();
            if (text.isEmpty()) {
                throw new ChatProviderException("Gemini returned empty text");
            }
            return text;
        } catch (ChatProviderException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new ChatProviderException("Gemini response could not be read", e);
        }
    }
}