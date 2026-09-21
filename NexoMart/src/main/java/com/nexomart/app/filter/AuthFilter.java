package com.nexomart.app.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.nexomart.app.model.User;

/**
 * Guards routes that require a logged-in session, and further restricts
 * seller-only routes (product creation/edit/delete) and admin-only routes (admin dashboard)
 * to users with the appropriate role.
 * Unauthenticated requests are redirected to the login page.
 */
@WebFilter(urlPatterns = {
        "/cart/*",
        "/checkout/*",
        "/orders/*",
        "/products/create",
        "/products/edit",
        "/products/delete",
        "/reviews/*",
        "/admin/*"
})
public class AuthFilter implements Filter {

    public static final String SESSION_USER_ATTR = "loggedInUser";

    @Override
    public void init(FilterConfig filterConfig) {
        // no-op
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        HttpSession session = request.getSession(false);
        User loggedInUser = session == null ? null : (User) session.getAttribute(SESSION_USER_ATTR);

        if (loggedInUser == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirectReason=auth");
            return;
        }

        String path = request.getServletPath();
        if (path.startsWith("/products/create") && loggedInUser.getRole() != User.Role.SELLER) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only sellers can create product listings.");
            return;
        }
        if ((path.startsWith("/products/edit") || path.startsWith("/products/delete"))
                && loggedInUser.getRole() != User.Role.SELLER) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only sellers can edit or delete product listings.");
            return;
        }
        if (path.startsWith("/admin") && loggedInUser.getRole() != User.Role.ADMIN) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin access only.");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // no-op
    }
}