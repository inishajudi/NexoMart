package com.nexomart.app.listener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.nexomart.app.util.PasswordUtil;

/**
 * Owns the single HikariCP connection pool for the whole application.
 * No other class should ever call DriverManager.getConnection() directly -
 * everyone gets connections through {@link #getDataSource(javax.servlet.ServletContext)}.
 */
@WebListener
public class DataSourceListener implements ServletContextListener {

    private static final Logger log = LoggerFactory.getLogger(DataSourceListener.class);

    public static final String DATASOURCE_ATTR = "com.nexomart.app.datasource";

    private HikariDataSource dataSource;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        HikariConfig config = new HikariConfig();
        // File-based H2 DB so data survives across requests/redeploys during dev.
        config.setJdbcUrl("jdbc:h2:file:./data/nexomart;AUTO_SERVER=TRUE");
        config.setUsername("sa");
        config.setPassword("");
        config.setDriverClassName("org.h2.Driver");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setPoolName("NexoMartPool");

        dataSource = new HikariDataSource(config);
        sce.getServletContext().setAttribute(DATASOURCE_ATTR, dataSource);

        runScript("/schema.sql", dataSource);
        if (isUsersTableEmpty(dataSource)) {
            runScript("/seed.sql", dataSource);
            log.info("NexoMart DataSource initialized; schema applied and seed data loaded.");
        } else {
            log.info("NexoMart DataSource initialized; schema applied, seed skipped (data already present).");
        }
        repairDemoPasswords(dataSource);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            log.info("NexoMart DataSource closed.");
        }
    }

    /** Retrieve the shared DataSource from anywhere the ServletContext is reachable. */
    public static javax.sql.DataSource getDataSource(javax.servlet.ServletContext context) {
        return (javax.sql.DataSource) context.getAttribute(DATASOURCE_ATTR);
    }

    private boolean isUsersTableEmpty(HikariDataSource ds) {
        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
            return rs.next() && rs.getInt(1) == 0;
        } catch (SQLException e) {
            // table probably doesn't exist yet on a fresh DB before schema.sql ran successfully; treat as empty
            return true;
        }
    }

    private void runScript(String classpathResource, HikariDataSource ds) {
        try (InputStream in = DataSourceListener.class.getResourceAsStream(classpathResource)) {
            if (in == null) {
                log.warn("SQL script {} not found on classpath, skipping.", classpathResource);
                return;
            }
            String sql = readFully(in);
            try (Connection conn = ds.getConnection();
                 Statement stmt = conn.createStatement()) {
                for (String statement : sql.split(";")) {
                    String trimmed = statement.trim();
                    if (!trimmed.isEmpty()) {
                        stmt.execute(trimmed);
                    }
                }
            }
        } catch (SQLException | IOException e) {
            log.error("Failed to run script {}", classpathResource, e);
            throw new RuntimeException("Failed to initialize database from " + classpathResource, e);
        }
    }

    private String readFully(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        }
        return sb.toString();
    }

    /**
     * The bundled database may outlive a deployment and can contain an older,
     * invalid demo hash. Repair only the documented demo accounts and only
     * when their password no longer verifies, leaving normal user passwords
     * and all marketplace data untouched.
     */
    private void repairDemoPasswords(HikariDataSource ds) {
        String[] demoEmails = {
                "buyer1@nexomart.com",
                "buyer2@nexomart.com",
                "seller1@nexomart.com",
                "seller2@nexomart.com",
                "seller3@nexomart.com",
                "seller4@nexomart.com",
                "admin@nexomart.com"
        };
        String selectSql = "SELECT password_hash FROM users WHERE LOWER(email) = ?";
        String updateSql = "UPDATE users SET password_hash = ? WHERE LOWER(email) = ?";
        String repairedHash = null;
        try (Connection conn = ds.getConnection();
             PreparedStatement select = conn.prepareStatement(selectSql);
             PreparedStatement update = conn.prepareStatement(updateSql)) {
            for (String email : demoEmails) {
                select.setString(1, email);
                try (java.sql.ResultSet rs = select.executeQuery()) {
                    if (rs.next() && !PasswordUtil.verify("nexomart123", rs.getString(1))) {
                        if (repairedHash == null) {
                            repairedHash = PasswordUtil.hash("nexomart123");
                        }
                        update.setString(1, repairedHash);
                        update.setString(2, email);
                        update.executeUpdate();
                        log.info("Repaired demo password for {}", email);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to validate demo account passwords", e);
        }
    }
}
