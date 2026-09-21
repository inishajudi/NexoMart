package com.nexomart.app.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nexomart.app.exception.AuthenticationException;
import com.nexomart.app.filter.AuthFilter;
import com.nexomart.app.model.User;

@WebServlet("/login")
public class LoginServlet extends BaseServlet {

    private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);

    /** Session expires after 30 minutes of inactivity. */
    private static final int SESSION_TIMEOUT_SECONDS = 30 * 60;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            User user = userService.authenticate(email, password);

            // Invalidate any pre-login session and start a fresh one to prevent
            // session fixation attacks - the session ID changes on privilege escalation.
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession session = request.getSession(true);
            session.setMaxInactiveInterval(SESSION_TIMEOUT_SECONDS);
            session.setAttribute(AuthFilter.SESSION_USER_ATTR, user);

            log.info("User {} logged in", user.getEmail());

            if (user.getRole() == User.Role.ADMIN) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/products");
            }
        } catch (AuthenticationException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("formEmail", email);
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }
}