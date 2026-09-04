package com.nexo.nexomart.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DAO tests run against an embedded H2 instance (jdbc:h2:mem:test), per Section 9's
 * testing matrix, exercising the checkout transaction in OrderDAOImpl.placeOrderFromCart.
 */
class OrderDAOImplTest {

    private HikariDataSource dataSource;
    private ProductDAO productDAO;
    private CartDAO cartDAO;
    private OrderDAO orderDAO;

    @BeforeEach
    void setUp() throws IOException, SQLException {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:ordertest;DB_CLOSE_DELAY=-1");
        config.setUsername("sa");
        config.setPassword("");
        config.setDriverClassName("org.h2.Driver");
        dataSource = new HikariDataSource(config);

        runSchema(dataSource);

        productDAO = new ProductDAOImpl(dataSource);
        cartDAO = new CartDAOImpl(dataSource);
        orderDAO = new OrderDAOImpl(dataSource);

        seedUsersAndProduct();
    }

    @AfterEach
    void tearDown() {
        dataSource.close();
    }

    @Test
    void placeOrderFromCart_movesCartIntoOrderAndDecrementsStock() {
        cartDAO.upsert(3L, 1L, 2); // buyer 3 adds 2x product 1 (stock 10, price 100.00)

        var order = orderDAO.placeOrderFromCart(3L);

        assertEquals(1, order.getItems().size());
        assertEquals(0, new BigDecimal("200.00").compareTo(order.getTotalAmount()));
        assertTrue(cartDAO.findByUser(3L).isEmpty(), "cart should be cleared after checkout");

        var product = productDAO.findById(1L).orElseThrow();
        assertEquals(8, product.getStockQty(), "stock should be decremented by the ordered quantity");
    }

    @Test
    void placeOrderFromCart_throwsWhenCartIsEmpty() {
        assertThrows(RuntimeException.class, () -> orderDAO.placeOrderFromCart(3L));
    }

    private void seedUsersAndProduct() throws SQLException {
        try (Connection con = dataSource.getConnection(); Statement st = con.createStatement()) {
            st.execute("INSERT INTO users (id, name, email, password_hash, role) VALUES "
                    + "(2, 'Seller', 's@test.local', 'x', 'SELLER'), "
                    + "(3, 'Buyer', 'b@test.local', 'x', 'BUYER')");
            st.execute("INSERT INTO products (id, seller_id, name, description, price, stock_qty, category) "
                    + "VALUES (1, 2, 'Test Product', 'desc', 100.00, 10, 'Test')");
        }
    }

    private void runSchema(DataSource ds) throws IOException, SQLException {
        String schemaSql = Files.readString(Path.of("db/migrations/V1__init_schema.sql"));
        try (Connection con = ds.getConnection(); Statement st = con.createStatement()) {
            for (String stmt : schemaSql.split(";")) {
                if (!stmt.isBlank()) {
                    st.execute(stmt);
                }
            }
        }
    }
}
