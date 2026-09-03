package com.nexo.nexomart.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Central Gson instance and small helpers so every servlet serializes JSON
 * the same way (envelope shape defined in dto.ApiResponse).
 */
public final class JsonUtil {

    private static final Gson GSON = new GsonBuilder().create();

    private JsonUtil() {
    }

    public static Gson gson() {
        return GSON;
    }

    public static <T> T fromRequestBody(HttpServletRequest request, Class<T> type) throws IOException {
        try (var reader = request.getReader()) {
            return GSON.fromJson(reader, type);
        }
    }

    public static void writeJson(HttpServletResponse response, int status, Object body) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.print(GSON.toJson(body));
        }
    }
}
