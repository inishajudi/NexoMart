package com.nexo.nexomart.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonPrimitive;
import java.time.format.DateTimeFormatter;

/** Central Gson instance + small helpers so servlets don't repeat JSON plumbing. */
public final class JsonUtil {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class,
                    (JsonSerializer<LocalDateTime>) (src, type, ctx) ->
                            new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .registerTypeAdapter(LocalDateTime.class,
                    (JsonDeserializer<LocalDateTime>) (json, type, ctx) ->
                            LocalDateTime.parse(json.getAsString()))
            .create();

    private JsonUtil() { }

    public static <T> T fromRequestBody(HttpServletRequest req, Class<T> clazz) throws IOException {
        try (var reader = req.getReader()) {
            return GSON.fromJson(reader, clazz);
        }
    }

    public static void writeJson(HttpServletResponse resp, int statusCode, Object body) throws IOException {
        resp.setStatus(statusCode);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(GSON.toJson(body));
    }
}
