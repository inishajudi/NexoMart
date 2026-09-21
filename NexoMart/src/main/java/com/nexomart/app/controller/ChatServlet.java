package com.nexomart.app.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nexomart.app.chat.ChatProvider;
import com.nexomart.app.chat.ChatProviderFactory;
import com.nexomart.app.chat.ChatService;
import com.nexomart.app.chat.RateLimitExceededException;
import com.nexomart.app.util.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * POST /api/v1/chat
 * Request : {"message": "How do I sell a product?"}
 * Response: {"reply": "..."}                       (200)
 *           {"error": "..."}                       (400 bad input, 429 rate limited)
 *
 * Public endpoint (FAQ assistant). Rate limiting is per HTTP session.
 * Provider problems never surface as an error page: ChatService returns a
 * static degraded reply instead.
 */
@WebServlet("/api/v1/chat")
public class ChatServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(ChatServlet.class);
    private static final Gson GSON = new Gson();
    private static final int MAX_BODY_CHARS = 4096;

    private ChatService chatService;

    @Override
    public void init() throws ServletException {
        ChatProvider provider = ChatProviderFactory.fromConfig();
        chatService = new ChatService(provider);
        log.info("Chat assistant started with provider '{}'", provider.name());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        try {
            String message = readMessage(request);
            HttpSession session = request.getSession(true);
            String reply = chatService.answer(session.getId(), message);
            Map<String, String> body = new LinkedHashMap<>();
            body.put("reply", reply);
            JsonUtil.writeJson(response, HttpServletResponse.SC_OK, body);
        } catch (IllegalArgumentException e) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (RateLimitExceededException e) {
            response.setHeader("Retry-After", String.valueOf(e.getRetryAfterSeconds()));
            writeError(response, 429, e.getMessage());
        } catch (RuntimeException e) {
            // Last line of defence: the widget must never get an error page.
            log.warn("Unexpected chat error: {}", e.toString());
            Map<String, String> body = new LinkedHashMap<>();
            body.put("reply", ChatService.DEGRADED_REPLY);
            JsonUtil.writeJson(response, HttpServletResponse.SC_OK, body);
        }
    }

    /** Reads the request body (size-capped) and extracts the "message" string field. */
    private static String readMessage(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        char[] buf = new char[512];
        BufferedReader reader = request.getReader();
        int n;
        while ((n = reader.read(buf)) != -1) {
            sb.append(buf, 0, n);
            if (sb.length() > MAX_BODY_CHARS) {
                throw new IllegalArgumentException("Request is too large.");
            }
        }

        JsonElement root;
        try {
            root = GSON.fromJson(sb.toString(), JsonElement.class);
        } catch (JsonParseException e) {
            throw new IllegalArgumentException("Request body must be valid JSON.");
        }
        if (root == null || !root.isJsonObject()) {
            throw new IllegalArgumentException("Request body must be a JSON object.");
        }
        JsonElement field = root.getAsJsonObject().get("message");
        if (field == null || !field.isJsonPrimitive() || !field.getAsJsonPrimitive().isString()) {
            throw new IllegalArgumentException("Field 'message' is required.");
        }
        return field.getAsString();
    }

    private static void writeError(HttpServletResponse response, int status, String message) throws IOException {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("error", message);
        JsonUtil.writeJson(response, status, body);
    }
}
