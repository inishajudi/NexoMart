package com.nexo.nexomart.dao;

import com.nexo.nexomart.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDAO {
    List<User> findAll();
    Optional<User> findById(long id);
}
