package com.nexomart.app.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nexomart.app.dto.RegisterRequest;
import com.nexomart.app.exception.ValidationException;
import com.nexomart.app.model.User;

@WebServlet("/register")
public class RegisterServlet extends BaseServlet {

    private static final Logger log = LoggerFactory.getLogger(RegisterServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RegisterRequest regRequest = new RegisterRequest(
                request.getParameter("name"),
                request.getParameter("email"),
                request.getParameter("password"),
                request.getParameter("role"));

        try {
            User user = userService.register(regRequest);
            log.info("Registered new {} account: {}", user.getRole(), user.getEmail());
            request.getSession().setAttribute("flashMessage", "Account created! Please log in.");
            response.sendRedirect(request.getContextPath() + "/login");
        } catch (ValidationException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("formName", regRequest.getName());
            request.setAttribute("formEmail", regRequest.getEmail());
            request.setAttribute("formRole", regRequest.getRole());
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
        }
    }
}
