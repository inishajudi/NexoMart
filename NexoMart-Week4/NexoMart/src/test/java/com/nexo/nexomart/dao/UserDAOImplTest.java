package com.nexo.nexomart.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOImplTest {

    private HikariDataSource dataSource;
    private UserDAO userDAO;

    @BeforeEach
    void setUp() throws IOException, SQLException {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:usertest;DB_CLOSE_DELAY=-1");
        config.setUsername("sa");
        config.setPassword("");
        config.setDriverClassName("org.h2.Driver");
        dataSource = new HikariDataSource(config);

        String schemaSql = Files.readString(Path.of("db/migrations/V1__init_schema.sql"));
        try (Connection con = dataSource.getConnection(); Statement st = con.createStatement()) {
            for (String stmt : schemaSql.split(";")) {
                if (!stmt.isBlank()) {
                    st.execute(stmt);
                }
            }
            st.execute("INSERT INTO users (id, name, email, password_hash, role) VALUES "
                    + "(1, 'Admin', 'admin@test.local', 'hash-not-selected', 'ADMIN'), "
                    + "(2, 'Seller', 'seller@test.local', 'hash-not-selected', 'SELLER')");
        }

        userDAO = new UserDAOImpl(dataSource);
    }

    @AfterEach
    void tearDown() {
        dataSource.close();
    }

    @Test
    void findAll_returnsEveryUser_withoutPasswordHash() {
        var users = userDAO.findAll();
        assertEquals(2, users.size());
        // User model does have a passwordHash field, but UserDAOImpl's SELECT never
        // fetches password_hash — confirm it comes back null, not the DB value.
        assertNull(users.get(0).getPasswordHash());
    }

    @Test
    void findById_returnsEmptyForUnknownId() {
        assertTrue(userDAO.findById(999L).isEmpty());
    }
}
