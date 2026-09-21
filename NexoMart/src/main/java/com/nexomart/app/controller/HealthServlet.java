package com.nexomart.app.controller;

import java.io.IOException;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.nexomart.app.listener.DataSourceListener;
import com.nexomart.app.util.JsonUtil;

/** GET /api/v1/health - reports whether the app and its database are up. */
@WebServlet("/api/v1/health")
public class HealthServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean dbUp = false;
        try {
            DataSource ds = DataSourceListener.getDataSource(getServletContext());
            if (ds != null) {
                try (Connection conn = ds.getConnection()) {
                    dbUp = conn.isValid(2);
                }
            }
        } catch (Exception e) {
            dbUp = false;
        }

        Map<String, String> body = new LinkedHashMap<>();
        body.put("status", dbUp ? "UP" : "DOWN");
        body.put("db", dbUp ? "UP" : "DOWN");
        JsonUtil.writeJson(response, dbUp ? HttpServletResponse.SC_OK : HttpServletResponse.SC_SERVICE_UNAVAILABLE, body);
    }
}