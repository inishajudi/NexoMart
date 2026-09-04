package com.nexo.nexomart.listener;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

/**
 * Single owner of the connection pool lifecycle (Section 2, rule 5 — no
 * DriverManager.getConnection() calls anywhere else in the app). Also implements the
 * Singleton design pattern requirement from Section 12.
 *
 * The pool is stashed in the ServletContext under DATASOURCE_ATTR; DAOs pull it out via
 * DataSourceProvider instead of talking to the context directly.
 */
@WebListener
public class DataSourceListener implements ServletContextListener {

    public static final String DATASOURCE_ATTR = "nexomart.datasource";

    private HikariDataSource dataSource;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        HikariConfig config = new HikariConfig();

        // For local/dev use embedded H2; for the deployed VM point at the server-mode
        // instance per Section 10, e.g. jdbc:h2:tcp://localhost:9092/./data/nexomart
        String jdbcUrl = System.getenv().getOrDefault(
                "NEXOMART_JDBC_URL", "jdbc:h2:mem:nexomart;DB_CLOSE_DELAY=-1");
        String user = System.getenv().getOrDefault("NEXOMART_DB_USER", "sa");
        String pass = System.getenv().getOrDefault("NEXOMART_DB_PASSWORD", "");

        config.setJdbcUrl(jdbcUrl);
        config.setUsername(user);
        config.setPassword(pass);
        config.setDriverClassName("org.h2.Driver");
        config.setMaximumPoolSize(10);
        config.setPoolName("nexomart-pool");

        dataSource = new HikariDataSource(config);
        sce.getServletContext().setAttribute(DATASOURCE_ATTR, dataSource);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
