package com.nexo.nexomart.listener;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

/** Thin lookup so DAOs don't depend directly on ServletContextListener internals. */
public final class DataSourceProvider {

    private DataSourceProvider() { }

    public static DataSource get(ServletContext context) {
        return (DataSource) context.getAttribute(DataSourceListener.DATASOURCE_ATTR);
    }
}
