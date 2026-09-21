package com.nexomart.app.controller;

import javax.servlet.http.HttpServlet;
import javax.sql.DataSource;

import com.nexomart.app.dao.CartDao;
import com.nexomart.app.dao.OrderDao;
import com.nexomart.app.dao.ProductDao;
import com.nexomart.app.dao.ReviewDao;
import com.nexomart.app.dao.UserDao;
import com.nexomart.app.dao.impl.JdbcCartDao;
import com.nexomart.app.dao.impl.JdbcOrderDao;
import com.nexomart.app.dao.impl.JdbcProductDao;
import com.nexomart.app.dao.impl.JdbcReviewDao;
import com.nexomart.app.dao.impl.JdbcUserDao;
import com.nexomart.app.listener.DataSourceListener;
import com.nexomart.app.service.CartService;
import com.nexomart.app.service.OrderService;
import com.nexomart.app.service.ProductService;
import com.nexomart.app.service.ReviewService;
import com.nexomart.app.service.UserService;

/**
 * Every controller servlet extends this to get access to the service layer,
 * wired against DAO interfaces (never concrete DB code) backed by the single
 * connection pool owned by {@link DataSourceListener}.
 */
public abstract class BaseServlet extends HttpServlet {

    protected UserService userService;
    protected ProductService productService;
    protected CartService cartService;
    protected OrderService orderService;
    protected ReviewService reviewService;

    @Override
    public void init() {
        DataSource dataSource = DataSourceListener.getDataSource(getServletContext());

        UserDao userDao = new JdbcUserDao(dataSource);
        ProductDao productDao = new JdbcProductDao(dataSource);
        CartDao cartDao = new JdbcCartDao(dataSource);
        OrderDao orderDao = new JdbcOrderDao(dataSource);
        ReviewDao reviewDao = new JdbcReviewDao(dataSource);

        this.userService = new UserService(userDao);
        this.productService = new ProductService(productDao);
        this.cartService = new CartService(cartDao, productDao);
        this.orderService = new OrderService(orderDao, cartDao, productDao);
        this.reviewService = new ReviewService(reviewDao, orderDao);
    }
}