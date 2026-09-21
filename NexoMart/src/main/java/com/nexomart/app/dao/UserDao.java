package com.nexomart.app.dao;

import java.util.List;
import java.util.Optional;

import com.nexomart.app.model.User;

public interface UserDao {

    User insert(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findById(long id);

    boolean existsByEmail(String email);

    List<User> findAll();
}