package com.nexomart.app.service;

import java.time.LocalDateTime;
import java.util.List;

import com.nexomart.app.dao.UserDao;
import com.nexomart.app.dto.RegisterRequest;
import com.nexomart.app.exception.AuthenticationException;
import com.nexomart.app.exception.ValidationException;
import com.nexomart.app.model.User;
import com.nexomart.app.util.PasswordUtil;
import com.nexomart.app.util.ValidationUtil;

/**
 * Business logic for user registration and authentication.
 */
public class UserService {

    private final UserDao userDao;

    /**
     * Constructs a UserService with the given UserDao.
     *
     * @param userDao the DAO used for user persistence
     */
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Registers a new user account after validating input and checking for duplicate email.
     *
     * @param request the registration details (name, email, password, role)
     * @return the newly created User
     * @throws ValidationException if input is invalid or the email is already registered
     */
    public User register(RegisterRequest request) throws ValidationException {
        ValidationUtil.requireNonBlank(request.getName(), "Name");
        ValidationUtil.requireValidEmail(request.getEmail());
        ValidationUtil.requireMinLength(request.getPassword(), 8, "Password");

        User.Role role;
        try {
            role = User.Role.valueOf(request.getRole());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ValidationException("Role must be BUYER or SELLER.");
        }

        if (userDao.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new ValidationException("An account with that email already exists.");
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPasswordHash(PasswordUtil.hash(request.getPassword()));
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());

        return userDao.insert(user);
    }

    /**
     * Authenticates a user by verifying their email and bcrypt-hashed password.
     *
     * @param email    the user's email address
     * @param password the plain-text password to verify
     * @return the authenticated User
     * @throws AuthenticationException if credentials are missing or do not match
     */
    public User authenticate(String email, String password) throws AuthenticationException {
        if (email == null || password == null) {
            throw new AuthenticationException("Email and password are required.");
        }
        User user = userDao.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new AuthenticationException("Invalid email or password."));

        if (!PasswordUtil.verify(password, user.getPasswordHash())) {
            throw new AuthenticationException("Invalid email or password.");
        }
        return user;
    }

    /**
     * Returns all registered users. Intended for admin use only.
     *
     * @return list of all users
     */
    public List<User> findAll() {
        return userDao.findAll();
    }
}