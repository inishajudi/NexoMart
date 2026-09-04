package com.nexo.nexomart.dao;

import com.nexo.nexomart.exception.DataAccessException;
import com.nexo.nexomart.model.Role;
import com.nexo.nexomart.model.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Read-only for now — Week 1's auth servlets own user creation/login. This DAO exists
 * for the admin panel's "view all users" requirement (F7); it deliberately never
 * selects/exposes password_hash outside this class.
 */
public class UserDAOImpl implements UserDAO {

    private final DataSource dataSource;

    public UserDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT id, name, email, role, created_at FROM users ORDER BY created_at DESC";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<User> users = new ArrayList<>();
            while (rs.next()) {
                users.add(mapRow(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list users", e);
        }
    }

    @Override
    public Optional<User> findById(long id) {
        String sql = "SELECT id, name, email, role, created_at FROM users WHERE id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to fetch user " + id, e);
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setName(rs.getString("name"));
        u.setEmail(rs.getString("email"));
        u.setRole(Role.valueOf(rs.getString("role")));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            u.setCreatedAt(ts.toLocalDateTime());
        }
        return u;
    }
}
