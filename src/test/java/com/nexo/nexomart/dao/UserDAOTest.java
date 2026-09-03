package com.nexo.nexomart.dao;

import com.nexo.nexomart.model.Role;
import com.nexo.nexomart.model.User;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DAO layer test per spec Section 9: JUnit 5 against
 * jdbc:h2:mem:test;DB_CLOSE_DELAY=-1, initialized from schema.sql.
 */
class UserDAOTest {

    private static HikariDataSource dataSource;
    private UserDAO userDAO;

    @BeforeAll
    static void setUpDataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.h2.Driver");
        config.setJdbcUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        config.setUsername("sa");
        config.setPassword("");
        dataSource = new HikariDataSource(config);

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = new String(
                    UserDAOTest.class.getClassLoader().getResourceAsStream("schema.sql").readAllBytes());
            for (String statement : sql.split(";")) {
                if (!statement.trim().isEmpty()) {
                    stmt.execute(statement.trim());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize test schema", e);
        }
    }

    @AfterAll
    static void tearDownDataSource() {
        dataSource.close();
    }

    @BeforeEach
    void setUp() {
        userDAO = new UserDAOImpl(dataSource);
        // Clean slate between tests.
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM users");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void insertAssignsGeneratedIdAndTimestamp() {
        User user = new User(null, "Ada Lovelace", "ada@example.com", "hashed", Role.BUYER, null);

        User saved = userDAO.insert(user);

        assertNotNull(saved.getId());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void findByEmailReturnsInsertedUser() {
        userDAO.insert(new User(null, "Grace Hopper", "grace@example.com", "hashed", Role.SELLER, null));

        Optional<User> found = userDAO.findByEmail("grace@example.com");

        assertTrue(found.isPresent());
        assertEquals("Grace Hopper", found.get().getName());
        assertEquals(Role.SELLER, found.get().getRole());
    }

    @Test
    void findByEmailReturnsEmptyWhenNotFound() {
        Optional<User> found = userDAO.findByEmail("nobody@example.com");

        assertTrue(found.isEmpty());
    }

    @Test
    void existsByEmailReflectsCurrentState() {
        assertFalse(userDAO.existsByEmail("new@example.com"));

        userDAO.insert(new User(null, "New User", "new@example.com", "hashed", Role.BUYER, null));

        assertTrue(userDAO.existsByEmail("new@example.com"));
    }

    @Test
    void findByIdReturnsInsertedUser() {
        User saved = userDAO.insert(new User(null, "Linus", "linus@example.com", "hashed", Role.BUYER, null));

        Optional<User> found = userDAO.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("linus@example.com", found.get().getEmail());
    }
}
