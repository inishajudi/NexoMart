package com.nexo.nexomart.service;

import com.nexo.nexomart.dao.UserDAO;
import com.nexo.nexomart.exception.ValidationException;
import com.nexo.nexomart.model.Role;
import com.nexo.nexomart.model.User;
import com.nexo.nexomart.util.PasswordUtil;
import com.nexo.nexomart.util.ValidationUtil;

import java.util.Optional;

/**
 * Business rules for registration and login. Contains no JDBC - depends only
 * on the UserDAO interface (spec Section 12, SOLID rule).
 */
public class AuthService {

    private final UserDAO userDAO;

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Registers a new Buyer or Seller. Admin accounts are never created here -
     * they are seeded directly (spec F1).
     */
    public User register(String name, String email, String password, Role role) throws ValidationException {
        if (ValidationUtil.isBlank(name)) {
            throw new ValidationException("name", "Name is required");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new ValidationException("email", "A valid email is required");
        }
        if (!ValidationUtil.isValidPassword(password)) {
            throw new ValidationException("password", "Password must be at least 8 characters");
        }
        if (role != Role.BUYER && role != Role.SELLER) {
            throw new ValidationException("role", "Role must be BUYER or SELLER");
        }
        if (userDAO.existsByEmail(email)) {
            throw new ValidationException("email", "An account with this email already exists");
        }

        String hash = PasswordUtil.hash(password);
        User user = new User(null, name.trim(), email.trim().toLowerCase(), hash, role, null);
        return userDAO.insert(user);
    }

    /**
     * Verifies credentials. Returns the matching user only on success.
     */
    public Optional<User> authenticate(String email, String password) throws ValidationException {
        if (ValidationUtil.isBlank(email) || ValidationUtil.isBlank(password)) {
            throw new ValidationException("credentials", "Email and password are required");
        }

        Optional<User> found = userDAO.findByEmail(email.trim().toLowerCase());
        if (found.isPresent() && PasswordUtil.matches(password, found.get().getPasswordHash())) {
            return found;
        }
        return Optional.empty();
    }
}
