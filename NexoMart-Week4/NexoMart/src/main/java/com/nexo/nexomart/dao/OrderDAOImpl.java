package com.nexo.nexomart.dao;

import com.nexo.nexomart.exception.DataAccessException;
import com.nexo.nexomart.model.Order;
import com.nexo.nexomart.model.OrderItem;
import com.nexo.nexomart.model.OrderStatus;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OrderDAOImpl implements OrderDAO {

    private final DataSource dataSource;

    public OrderDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Order placeOrderFromCart(long buyerId) {
        String selectCart =
                "SELECT ci.product_id, ci.quantity, p.price, p.stock_qty, p.name "
              + "FROM cart_items ci JOIN products p ON p.id = ci.product_id "
              + "WHERE ci.user_id = ? FOR UPDATE";
        String insertOrder = "INSERT INTO orders (buyer_id, status, total_amount) VALUES (?, ?, ?)";
        String insertItem = "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
        String updateStock = "UPDATE products SET stock_qty = stock_qty - ? WHERE id = ? AND stock_qty >= ?";
        String clearCart = "DELETE FROM cart_items WHERE user_id = ?";

        try (Connection con = dataSource.getConnection()) {
            con.setAutoCommit(false);
            try {
                List<OrderItem> lines = new ArrayList<>();
                BigDecimal total = BigDecimal.ZERO;

                try (PreparedStatement ps = con.prepareStatement(selectCart)) {
                    ps.setLong(1, buyerId);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            long productId = rs.getLong("product_id");
                            int qty = rs.getInt("quantity");
                            int stock = rs.getInt("stock_qty");
                            BigDecimal price = rs.getBigDecimal("price");

                            if (qty > stock) {
                                throw new DataAccessException(
                                        "Insufficient stock for product '" + rs.getString("name")
                                                + "' (requested " + qty + ", available " + stock + ")", null);
                            }
                            lines.add(new OrderItem(productId, qty, price));
                            total = total.add(price.multiply(BigDecimal.valueOf(qty)));
                        }
                    }
                }

                if (lines.isEmpty()) {
                    throw new DataAccessException("Cannot place an order from an empty cart", null);
                }

                long orderId;
                try (PreparedStatement ps = con.prepareStatement(insertOrder, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, buyerId);
                    ps.setString(2, OrderStatus.PENDING.name());
                    ps.setBigDecimal(3, total);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        orderId = keys.getLong(1);
                    }
                }

                try (PreparedStatement itemPs = con.prepareStatement(insertItem);
                     PreparedStatement stockPs = con.prepareStatement(updateStock)) {
                    for (OrderItem line : lines) {
                        itemPs.setLong(1, orderId);
                        itemPs.setLong(2, line.getProductId());
                        itemPs.setInt(3, line.getQuantity());
                        itemPs.setBigDecimal(4, line.getUnitPrice());
                        itemPs.addBatch();

                        stockPs.setInt(1, line.getQuantity());
                        stockPs.setLong(2, line.getProductId());
                        stockPs.setInt(3, line.getQuantity());
                        stockPs.addBatch();
                    }
                    itemPs.executeBatch();
                    int[] stockResults = stockPs.executeBatch();
                    for (int updated : stockResults) {
                        if (updated == 0) {
                            throw new DataAccessException(
                                    "Stock changed concurrently; unable to reserve requested quantity", null);
                        }
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(clearCart)) {
                    ps.setLong(1, buyerId);
                    ps.executeUpdate();
                }

                con.commit();

                Order order = new Order();
                order.setId(orderId);
                order.setBuyerId(buyerId);
                order.setStatus(OrderStatus.PENDING);
                order.setTotalAmount(total);
                order.setItems(lines);
                return order;

            } catch (RuntimeException | SQLException ex) {
                con.rollback();
                if (ex instanceof DataAccessException dae) {
                    throw dae;
                }
                throw new DataAccessException("Checkout transaction failed", ex);
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to obtain connection for checkout", e);
        }
    }

    @Override
    public Optional<Order> findById(long orderId) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                Order order = mapOrderRow(rs);
                order.setItems(fetchItems(con, orderId));
                return Optional.of(order);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to fetch order " + orderId, e);
        }
    }

    @Override
    public List<Order> findByBuyer(long buyerId) {
        String sql = "SELECT * FROM orders WHERE buyer_id = ? ORDER BY created_at DESC";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, buyerId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Order> orders = new ArrayList<>();
                while (rs.next()) {
                    Order o = mapOrderRow(rs);
                    o.setItems(fetchItems(con, o.getId()));
                    orders.add(o);
                }
                return orders;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list orders for buyer " + buyerId, e);
        }
    }

    @Override
    public List<Order> findBySeller(long sellerId) {
        String sql = "SELECT DISTINCT o.* FROM orders o "
                + "JOIN order_items oi ON oi.order_id = o.id "
                + "JOIN products p ON p.id = oi.product_id "
                + "WHERE p.seller_id = ? ORDER BY o.created_at DESC";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Order> orders = new ArrayList<>();
                while (rs.next()) {
                    Order o = mapOrderRow(rs);
                    o.setItems(fetchItems(con, o.getId()));
                    orders.add(o);
                }
                return orders;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list orders for seller " + sellerId, e);
        }
    }

    @Override
    public List<Order> findAll() {
        String sql = "SELECT * FROM orders ORDER BY created_at DESC";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Order> orders = new ArrayList<>();
            while (rs.next()) {
                Order o = mapOrderRow(rs);
                o.setItems(fetchItems(con, o.getId()));
                orders.add(o);
            }
            return orders;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list all orders", e);
        }
    }

    private List<OrderItem> fetchItems(Connection con, long orderId) throws SQLException {
        String sql = "SELECT * FROM order_items WHERE order_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                List<OrderItem> items = new ArrayList<>();
                while (rs.next()) {
                    OrderItem oi = new OrderItem();
                    oi.setId(rs.getLong("id"));
                    oi.setOrderId(rs.getLong("order_id"));
                    oi.setProductId(rs.getLong("product_id"));
                    oi.setQuantity(rs.getInt("quantity"));
                    oi.setUnitPrice(rs.getBigDecimal("unit_price"));
                    items.add(oi);
                }
                return items;
            }
        }
    }

    private Order mapOrderRow(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getLong("id"));
        o.setBuyerId(rs.getLong("buyer_id"));
        o.setStatus(OrderStatus.valueOf(rs.getString("status")));
        o.setTotalAmount(rs.getBigDecimal("total_amount"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            o.setCreatedAt(ts.toLocalDateTime());
        }
        return o;
    }
}
