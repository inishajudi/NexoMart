package com.nexo.nexomart.filter;

import com.nexo.nexomart.dto.ApiResponse;
import com.nexo.nexomart.util.JsonUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Blocks access to any URL it is mapped to (see web.xml) unless a valid,
 * logged-in session exists. JSON routes get a 401 envelope; page routes
 * get redirected to /login.
 */
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        HttpSession session = request.getSession(false);
        boolean loggedIn = session != null && session.getAttribute("userId") != null;

        if (loggedIn) {
            chain.doFilter(req, res);
            return;
        }

        if (request.getRequestURI().contains("/api/")) {
            JsonUtil.writeJson(response, HttpServletResponse.SC_UNAUTHORIZED,
                    ApiResponse.fail("UNAUTHENTICATED", "Login required"));
        } else {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }
    }
}
