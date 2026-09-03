package com.nexo.nexomart.listener;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Properties;

/**
 * Owns the single HikariCP DataSource for the whole application, per Section 2
 * rule 5: "Connection pool lifecycle owned by a single ServletContextListener.
 * No DriverManager.getConnection() calls outside this listener."
 *
 * On startup it also runs schema.sql (idempotent, uses CREATE TABLE IF NOT EXISTS)
 * so a fresh H2 file/embedded DB is always usable immediately after deploy.
 */
@WebListener
public class DataSourceListener implements ServletContextListener {

    private static final Logger log = LoggerFactory.getLogger(DataSourceListener.class);
    private static HikariDataSource dataSource;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Properties props = loadConfig();

        String jdbcUrl = props.getProperty("db.url", "jdbc:h2:./data/nexomart;AUTO_SERVER=TRUE");
        String user = props.getProperty("db.user", "sa");
        String password = props.getProperty("db.password", "");

        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.h2.Driver");
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(user);
        config.setPassword(password);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setPoolName("NexoMartPool");

        dataSource = new HikariDataSource(config);
        log.info("HikariCP pool initialized: {}", jdbcUrl);

        runSchemaScript();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (dataSource != null) {
            dataSource.close();
            log.info("HikariCP pool closed");
        }
    }

    public static DataSource getDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource not initialized - is DataSourceListener registered in web.xml?");
        }
        return dataSource;
    }

    private Properties loadConfig() {
        Properties props = new Properties();
        // config.properties is intentionally .gitignore'd - see .env.example for the template.
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (in != null) {
                props.load(in);
            } else {
                log.warn("config.properties not found on classpath - falling back to embedded H2 defaults. "
                        + "Copy config.properties.example to config.properties for local overrides.");
            }
        } catch (IOException e) {
            log.error("Failed to load config.properties, using defaults", e);
        }
        return props;
    }

    private void runSchemaScript() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            InputStream in = getClass().getClassLoader().getResourceAsStream("schema.sql");
            if (in == null) {
                log.warn("schema.sql not found on classpath - skipping auto-schema-init");
                return;
            }
            String sql = new String(in.readAllBytes());
            for (String statement : sql.split(";")) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty()) {
                    stmt.execute(trimmed);
                }
            }
            log.info("schema.sql applied successfully");
        } catch (Exception e) {
            log.error("Failed to apply schema.sql", e);
        }
    }
}
