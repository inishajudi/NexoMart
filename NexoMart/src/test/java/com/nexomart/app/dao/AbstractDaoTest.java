package com.nexomart.app.dao;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/** Spins up a fresh in-memory H2 database (with schema applied) before every test. */
public abstract class AbstractDaoTest {

    protected DataSource dataSource;
    private HikariDataSource hikariDataSource;

    @BeforeEach
    void setUpDatabase() throws Exception {
        HikariConfig config = new HikariConfig();
        // Unique DB name per test run keeps tests isolated from each other.
        config.setJdbcUrl("jdbc:h2:mem:test_" + System.nanoTime() + ";DB_CLOSE_DELAY=-1");
        config.setUsername("sa");
        config.setPassword("");
        config.setDriverClassName("org.h2.Driver");
        config.setMaximumPoolSize(5);
        hikariDataSource = new HikariDataSource(config);
        dataSource = hikariDataSource;

        runScript("/schema.sql");
    }

    @AfterEach
    void tearDownDatabase() {
        if (hikariDataSource != null) {
            hikariDataSource.close();
        }
    }

    private void runScript(String classpathResource) throws SQLException, IOException {
        try (InputStream in = AbstractDaoTest.class.getResourceAsStream(classpathResource)) {
            if (in == null) {
                throw new IllegalStateException("Missing test resource: " + classpathResource);
            }
            StringBuilder sb = new StringBuilder();
            try (java.io.BufferedReader reader =
                         new java.io.BufferedReader(new java.io.InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
            }
            try (Connection conn = dataSource.getConnection();
                 Statement stmt = conn.createStatement()) {
                for (String statement : sb.toString().split(";")) {
                    String trimmed = statement.trim();
                    if (!trimmed.isEmpty()) {
                        stmt.execute(trimmed);
                    }
                }
            }
        }
    }
}
