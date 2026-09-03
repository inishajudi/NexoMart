package com.nexo.nexomart.filter;

import com.nexo.nexomart.dto.ApiResponse;
import com.nexo.nexomart.util.JsonUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Enforces the session check on every /api/v1/cart/* and /api/v1/orders/* route, per
 * the security checklist in Section 9. Public read-only browsing (/api/v1/products/*
 * GET) is intentionally NOT behind this filter so F3 works for anonymous visitors.
 *
 * This assumes Week 1 already sets session attributes "userId" and "role" on login.
 */
@WebFilter(urlPatterns = {"/api/v1/cart/*", "/api/v1/orders/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_UNAUTHORIZED,
                    ApiResponse.fail("UNAUTHENTICATED", "Login required"));
            return;
        }
        chain.doFilter(request, response);
    }
}
