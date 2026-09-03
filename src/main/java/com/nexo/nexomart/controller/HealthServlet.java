package com.nexo.nexomart.controller;

import com.nexo.nexomart.listener.DataSourceListener;
import com.nexo.nexomart.util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * GET /api/v1/health -> { "status": "UP", "db": "UP" }
 * Handy for confirming Tomcat + DB are both reachable, locally and once deployed
 * (spec Section 18 rule 1).
 */
@WebServlet("/api/v1/health")
public class HealthServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws java.io.IOException {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("status", "UP");
        try (Connection conn = DataSourceListener.getDataSource().getConnection()) {
            body.put("db", conn.isValid(2) ? "UP" : "DOWN");
        } catch (Exception e) {
            body.put("db", "DOWN");
        }
        JsonUtil.writeJson(resp, HttpServletResponse.SC_OK, body);
    }
}
