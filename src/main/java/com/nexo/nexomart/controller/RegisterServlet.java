package com.nexo.nexomart.controller;

import com.nexo.nexomart.dao.UserDAO;
import com.nexo.nexomart.dao.UserDAOImpl;
import com.nexo.nexomart.exception.ValidationException;
import com.nexo.nexomart.listener.DataSourceListener;
import com.nexo.nexomart.model.Role;
import com.nexo.nexomart.model.User;
import com.nexo.nexomart.service.AuthService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Thin controller: no SQL, no business logic. Delegates to AuthService.
 * GET  /register -> shows the form
 * POST /register -> creates the account
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private AuthService authService;

    @Override
    public void init() {
        UserDAO userDAO = new UserDAOImpl(DataSourceListener.getDataSource());
        this.authService = new AuthService(userDAO);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String roleParam = req.getParameter("role");

        Role role;
        try {
            role = Role.valueOf(roleParam == null ? "" : roleParam.toUpperCase());
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", "Please select a valid role");
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
            return;
        }

        try {
            User created = authService.register(name, email, password, role);
            // Auto-login after successful registration, with session fixation protection.
            req.getSession().invalidate();
            req.getSession(true).setAttribute("userId", created.getId());
            req.getSession().setAttribute("userName", created.getName());
            req.getSession().setAttribute("userRole", created.getRole().name());
            resp.sendRedirect(req.getContextPath() + "/dashboard");

        } catch (ValidationException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("name", name);
            req.setAttribute("email", email);
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
        }
    }
}
