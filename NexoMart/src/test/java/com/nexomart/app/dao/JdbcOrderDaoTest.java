package com.nexomart.app.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nexomart.app.dao.impl.JdbcOrderDao;
import com.nexomart.app.dao.impl.JdbcProductDao;
import com.nexomart.app.dao.impl.JdbcUserDao;
import com.nexomart.app.model.Order;
import com.nexomart.app.model.OrderItem;
import com.nexomart.app.model.Product;
import com.nexomart.app.model.User;

class JdbcOrderDaoTest extends AbstractDaoTest {

    private long buyerId;
    private long productId;

    @BeforeEach
    void seedBuyerAndProduct() {
        UserDao userDao = new JdbcUserDao(dataSource);
        User buyer = new User();
        buyer.setName("Test Buyer");
        buyer.setEmail("order.buyer@nexomart.test");
        buyer.setPasswordHash("dummy-hash");
        buyer.setRole(User.Role.BUYER);
        buyer.setCreatedAt(LocalDateTime.now());
        buyerId = userDao.insert(buyer).getId();

        User seller = new User();
        seller.setName("Test Seller");
        seller.setEmail("order.seller@nexomart.test");
        seller.setPasswordHash("dummy-hash");
        seller.setRole(User.Role.SELLER);
        seller.setCreatedAt(LocalDateTime.now());
        long sellerId = userDao.insert(seller).getId();

        ProductDao productDao = new JdbcProductDao(dataSource);
        Product product = new Product();
        product.setSellerId(sellerId);
        product.setName("Test Product");
        product.setDescription("desc");
        product.setPrice(new BigDecimal("250.00"));
        product.setStockQty(10);
        product.setCategory("Misc");
        product.setImageUrl("");
        product.setCreatedAt(LocalDateTime.now());
        productId = productDao.insert(product).getId();
    }

    @Test
    void insertWithItemsPersistsOrderAndLineItemsTogether() {
        OrderDao dao = new JdbcOrderDao(dataSource);

        Order order = new Order();
        order.setBuyerId(buyerId);
        order.setStatus(Order.Status.CONFIRMED);
        order.setTotalAmount(new BigDecimal("500.00"));
        order.setCreatedAt(LocalDateTime.now());

        OrderItem item = new OrderItem();
        item.setProductId(productId);
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("250.00"));
        order.setItems(List.of(item));

        Order saved = dao.insertWithItems(order);

        assertTrue(saved.getId() > 0);

        Order reloaded = dao.findByIdWithItems(saved.getId()).orElseThrow();
        assertEquals(1, reloaded.getItems().size());
        assertEquals(2, reloaded.getItems().get(0).getQuantity());
        assertEquals(new BigDecimal("500.00"), reloaded.getTotalAmount());
    }

    @Test
    void findByBuyerReturnsOnlyThatBuyersOrders() {
        OrderDao dao = new JdbcOrderDao(dataSource);

        Order order = new Order();
        order.setBuyerId(buyerId);
        order.setStatus(Order.Status.CONFIRMED);
        order.setTotalAmount(new BigDecimal("250.00"));
        order.setCreatedAt(LocalDateTime.now());
        OrderItem item = new OrderItem();
        item.setProductId(productId);
        item.setQuantity(1);
        item.setUnitPrice(new BigDecimal("250.00"));
        order.setItems(List.of(item));
        dao.insertWithItems(order);

        List<Order> history = dao.findByBuyer(buyerId);
        assertEquals(1, history.size());

        List<Order> otherBuyerHistory = dao.findByBuyer(buyerId + 999);
        assertEquals(0, otherBuyerHistory.size());
    }
}
