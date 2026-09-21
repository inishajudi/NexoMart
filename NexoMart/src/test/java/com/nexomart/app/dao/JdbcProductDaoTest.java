package com.nexomart.app.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nexomart.app.dao.impl.JdbcProductDao;
import com.nexomart.app.dao.impl.JdbcUserDao;
import com.nexomart.app.model.Product;
import com.nexomart.app.model.User;

class JdbcProductDaoTest extends AbstractDaoTest {

    private long sellerId;

    @BeforeEach
    void createSeller() {
        UserDao userDao = new JdbcUserDao(dataSource);
        User seller = new User();
        seller.setName("Seller One");
        seller.setEmail("seller.one@nexomart.test");
        seller.setPasswordHash("dummy-hash");
        seller.setRole(User.Role.SELLER);
        seller.setCreatedAt(LocalDateTime.now());
        sellerId = userDao.insert(seller).getId();
    }

    @Test
    void insertAndFindById() {
        ProductDao dao = new JdbcProductDao(dataSource);
        Product product = sampleProduct("Desk Lamp", "Lighting", new BigDecimal("799.00"), 10);

        Product saved = dao.insert(product);

        assertTrue(saved.getId() > 0);
        assertEquals("Desk Lamp", dao.findById(saved.getId()).orElseThrow().getName());
    }

    @Test
    void searchFiltersByKeywordAndCategory() {
        ProductDao dao = new JdbcProductDao(dataSource);
        dao.insert(sampleProduct("Wireless Mouse", "Electronics", new BigDecimal("599.00"), 20));
        dao.insert(sampleProduct("Frying Pan", "Home & Kitchen", new BigDecimal("899.00"), 15));

        List<Product> byKeyword = dao.search("mouse", null);
        assertEquals(1, byKeyword.size());
        assertEquals("Wireless Mouse", byKeyword.get(0).getName());

        List<Product> byCategory = dao.search(null, "Home & Kitchen");
        assertEquals(1, byCategory.size());
        assertEquals("Frying Pan", byCategory.get(0).getName());
    }

    @Test
    void decrementStockReducesQuantityAndRejectsOverdraw() {
        ProductDao dao = new JdbcProductDao(dataSource);
        Product saved = dao.insert(sampleProduct("Limited Item", "Misc", new BigDecimal("100.00"), 5));

        dao.decrementStock(saved.getId(), 3);
        assertEquals(2, dao.findById(saved.getId()).orElseThrow().getStockQty());

        org.junit.jupiter.api.Assertions.assertThrows(
                com.nexomart.app.exception.DataAccessException.class,
                () -> dao.decrementStock(saved.getId(), 10));
    }

    private Product sampleProduct(String name, String category, BigDecimal price, int stock) {
        Product product = new Product();
        product.setSellerId(sellerId);
        product.setName(name);
        product.setDescription("Sample description for " + name);
        product.setPrice(price);
        product.setStockQty(stock);
        product.setCategory(category);
        product.setImageUrl("https://example.test/img.png");
        product.setCreatedAt(LocalDateTime.now());
        return product;
    }
}
