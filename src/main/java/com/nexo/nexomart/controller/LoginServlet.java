package com.nexo.nexomart.controller;

import com.nexo.nexomart.dao.UserDAO;
import com.nexo.nexomart.dao.UserDAOImpl;
import com.nexo.nexomart.exception.ValidationException;
import com.nexo.nexomart.listener.DataSourceListener;
import com.nexo.nexomart.model.User;
import com.nexo.nexomart.service.AuthService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

/**
 * GET  /login -> shows the form
 * POST /login -> authenticates and starts a session
 *
 * Session ID is regenerated on every successful login (spec Section 2 rule 3)
 * to prevent session fixation: we invalidate any pre-existing session before
 * creating the new, authenticated one.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private AuthService authService;

    @Override
    public void init() {
        UserDAO userDAO = new UserDAOImpl(DataSourceListener.getDataSource());
        this.authService = new AuthService(userDAO);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        try {
            Optional<User> authenticated = authService.authenticate(email, password);

            if (authenticated.isEmpty()) {
                req.setAttribute("error", "Invalid email or password");
                req.setAttribute("email", email);
                req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
                return;
            }

            User user = authenticated.get();

            // Regenerate session ID on login to prevent fixation attacks.
            HttpSession existing = req.getSession(false);
            if (existing != null) {
                existing.invalidate();
            }
            HttpSession session = req.getSession(true);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userName", user.getName());
            session.setAttribute("userRole", user.getRole().name());

            resp.sendRedirect(req.getContextPath() + "/dashboard");

        } catch (ValidationException e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
        }
    }
}
