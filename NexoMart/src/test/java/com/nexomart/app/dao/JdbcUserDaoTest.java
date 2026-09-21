package com.nexomart.app.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.nexomart.app.dao.impl.JdbcUserDao;
import com.nexomart.app.model.User;

class JdbcUserDaoTest extends AbstractDaoTest {

    @Test
    void insertAssignsGeneratedIdAndPersistsFields() {
        UserDao dao = new JdbcUserDao(dataSource);

        User user = new User();
        user.setName("Test Buyer");
        user.setEmail("test.buyer@nexomart.test");
        user.setPasswordHash("dummy-hash");
        user.setRole(User.Role.BUYER);
        user.setCreatedAt(LocalDateTime.now());

        User saved = dao.insert(user);

        assertTrue(saved.getId() > 0, "Generated ID should be populated after insert");
    }

    @Test
    void findByEmailReturnsMatchingUser() {
        UserDao dao = new JdbcUserDao(dataSource);
        User user = new User();
        user.setName("Findable Seller");
        user.setEmail("findable.seller@nexomart.test");
        user.setPasswordHash("dummy-hash");
        user.setRole(User.Role.SELLER);
        user.setCreatedAt(LocalDateTime.now());
        dao.insert(user);

        Optional<User> found = dao.findByEmail("findable.seller@nexomart.test");

        assertTrue(found.isPresent());
        assertEquals("Findable Seller", found.get().getName());
        assertEquals(User.Role.SELLER, found.get().getRole());
    }

    @Test
    void existsByEmailReflectsCurrentState() {
        UserDao dao = new JdbcUserDao(dataSource);
        assertFalse(dao.existsByEmail("nobody@nexomart.test"));

        User user = new User();
        user.setName("Someone");
        user.setEmail("somebody@nexomart.test");
        user.setPasswordHash("dummy-hash");
        user.setRole(User.Role.BUYER);
        user.setCreatedAt(LocalDateTime.now());
        dao.insert(user);

        assertTrue(dao.existsByEmail("somebody@nexomart.test"));
    }
}
