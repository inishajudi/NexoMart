package com.nexo.nexomart.dao;

import com.nexo.nexomart.model.User;

import java.util.Optional;

/**
 * Data access abstraction for the users table. The service layer depends on
 * this interface, never on UserDAOImpl directly (spec Section 12, SOLID rule).
 */
public interface UserDAO {

    User insert(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    boolean existsByEmail(String email);
}
