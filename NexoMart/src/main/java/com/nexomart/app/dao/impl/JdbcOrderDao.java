package com.nexomart.app.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import com.nexomart.app.dao.OrderDao;
import com.nexomart.app.exception.DataAccessException;
import com.nexomart.app.model.Order;
import com.nexomart.app.model.OrderItem;

public class JdbcOrderDao implements OrderDao {

    private final DataSource dataSource;

    public JdbcOrderDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Order insertWithItems(Order order) {
        String insertOrderSql = "INSERT INTO orders (buyer_id, status, total_amount, created_at) VALUES (?, ?, ?, ?)";
        String insertItemSql = "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, order.getBuyerId());
                    ps.setString(2, order.getStatus().name());
                    ps.setBigDecimal(3, order.getTotalAmount());
                    ps.setTimestamp(4, Timestamp.valueOf(order.getCreatedAt()));
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            order.setId(keys.getLong(1));
                        }
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(insertItemSql, Statement.RETURN_GENERATED_KEYS)) {
                    for (OrderItem item : order.getItems()) {
                        ps.setLong(1, order.getId());
                        ps.setLong(2, item.getProductId());
                        ps.setInt(3, item.getQuantity());
                        ps.setBigDecimal(4, item.getUnitPrice());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        int i = 0;
                        while (keys.next() && i < order.getItems().size()) {
                            order.getItems().get(i).setId(keys.getLong(1));
                            order.getItems().get(i).setOrderId(order.getId());
                            i++;
                        }
                    }
                }

                conn.commit();
                return order;
            } catch (SQLException e) {
                conn.rollback();
                throw new DataAccessException("Failed to place order, transaction rolled back", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to obtain connection for order placement", e);
        }
    }

    @Override
    public Optional<Order> findByIdWithItems(long orderId) {
        String orderSql = "SELECT id, buyer_id, status, total_amount, created_at FROM orders WHERE id = ?";
        try (Connection conn = dataSource.getConnection()) {
            Order order;
            try (PreparedStatement ps = conn.prepareStatement(orderSql)) {
                ps.setLong(1, orderId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return Optional.empty();
                    }
                    order = mapOrderRow(rs);
                }
            }
            order.setItems(fetchItems(conn, order.getId()));
            return Optional.of(order);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find order by id", e);
        }
    }

    @Override
    public List<Order> findByBuyer(long buyerId) {
        String sql = "SELECT id, buyer_id, status, total_amount, created_at FROM orders WHERE buyer_id = ? ORDER BY created_at DESC";
        try (Connection conn = dataSource.getConnection()) {
            Map<Long, Order> orders = new LinkedHashMap<>();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, buyerId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Order order = mapOrderRow(rs);
                        orders.put(order.getId(), order);
                    }
                }
            }
            for (Order order : orders.values()) {
                order.setItems(fetchItems(conn, order.getId()));
            }
            return new ArrayList<>(orders.values());
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find orders for buyer", e);
        }
    }

    @Override
    public List<Order> findAll() {
        String sql = "SELECT id, buyer_id, status, total_amount, created_at FROM orders ORDER BY created_at DESC";
        try (Connection conn = dataSource.getConnection()) {
            Map<Long, Order> orders = new LinkedHashMap<>();
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = mapOrderRow(rs);
                    orders.put(order.getId(), order);
                }
            }
            for (Order order : orders.values()) {
                order.setItems(fetchItems(conn, order.getId()));
            }
            return new ArrayList<>(orders.values());
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find all orders", e);
        }
    }

    @Override
    public List<Order> findBySeller(long sellerId) {
        String sql = "SELECT DISTINCT o.id, o.buyer_id, o.status, o.total_amount, o.created_at "
                + "FROM orders o "
                + "JOIN order_items oi ON oi.order_id = o.id "
                + "JOIN products p ON p.id = oi.product_id "
                + "WHERE p.seller_id = ? "
                + "ORDER BY o.created_at DESC";
        try (Connection conn = dataSource.getConnection()) {
            Map<Long, Order> orders = new LinkedHashMap<>();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, sellerId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Order order = mapOrderRow(rs);
                        orders.put(order.getId(), order);
                    }
                }
            }
            for (Order order : orders.values()) {
                order.setItems(fetchItemsForSeller(conn, order.getId(), sellerId));
            }
            return new ArrayList<>(orders.values());
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find orders for seller", e);
        }
    }

    @Override
    public boolean hasDeliveredOrderForProduct(long buyerId, long productId) {
        // NOTE: Orders currently reach CONFIRMED at checkout and there is no status-update
        // flow yet to move them to DELIVERED. Until that exists, CONFIRMED is treated as
        // "completed enough" to leave a review. Tighten this to DELIVERED-only once
        // order status progression is implemented.
        String sql = "SELECT COUNT(*) FROM orders o "
                + "JOIN order_items oi ON oi.order_id = o.id "
                + "WHERE o.buyer_id = ? AND oi.product_id = ? "
                + "AND o.status IN ('CONFIRMED', 'SHIPPED', 'DELIVERED')";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, buyerId);
            ps.setLong(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to check delivered order for product", e);
        }
    }

    private List<OrderItem> fetchItems(Connection conn, long orderId) throws SQLException {
        String sql = "SELECT oi.id, oi.order_id, oi.product_id, oi.quantity, oi.unit_price, p.name AS product_name "
                + "FROM order_items oi JOIN products p ON oi.product_id = p.id WHERE oi.order_id = ?";
        List<OrderItem> items = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapOrderItemRow(rs));
                }
            }
        }
        return items;
    }

    private List<OrderItem> fetchItemsForSeller(Connection conn, long orderId, long sellerId) throws SQLException {
        String sql = "SELECT oi.id, oi.order_id, oi.product_id, oi.quantity, oi.unit_price, p.name AS product_name "
                + "FROM order_items oi JOIN products p ON oi.product_id = p.id "
                + "WHERE oi.order_id = ? AND p.seller_id = ?";
        List<OrderItem> items = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            ps.setLong(2, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapOrderItemRow(rs));
                }
            }
        }
        return items;
    }

    private OrderItem mapOrderItemRow(ResultSet rs) throws SQLException {
        OrderItem item = new OrderItem();
        item.setId(rs.getLong("id"));
        item.setOrderId(rs.getLong("order_id"));
        item.setProductId(rs.getLong("product_id"));
        item.setQuantity(rs.getInt("quantity"));
        item.setUnitPrice(rs.getBigDecimal("unit_price"));
        item.setProductName(rs.getString("product_name"));
        return item;
    }

    private Order mapOrderRow(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setBuyerId(rs.getLong("buyer_id"));
        order.setStatus(Order.Status.valueOf(rs.getString("status")));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return order;
    }
}