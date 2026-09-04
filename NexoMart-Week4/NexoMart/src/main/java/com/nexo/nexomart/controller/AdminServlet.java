package com.nexo.nexomart.controller;

import com.nexo.nexomart.dao.OrderDAO;
import com.nexo.nexomart.dao.OrderDAOImpl;
import com.nexo.nexomart.dao.ProductDAO;
import com.nexo.nexomart.dao.ProductDAOImpl;
import com.nexo.nexomart.dao.UserDAO;
import com.nexo.nexomart.dao.UserDAOImpl;
import com.nexo.nexomart.dto.ApiResponse;
import com.nexo.nexomart.exception.NotFoundException;
import com.nexo.nexomart.listener.DataSourceProvider;
import com.nexo.nexomart.service.AdminService;
import com.nexo.nexomart.service.ProductService;
import com.nexo.nexomart.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * GET    /api/v1/admin/users              -> all users (F7)
 * GET    /api/v1/admin/orders              -> all orders (F7)
 * DELETE /api/v1/admin/products/{id}       -> moderate/remove any listing (F7)
 *
 * Sits behind AuthFilter (session required) AND re-checks role == ADMIN itself, since
 * AuthFilter only proves "logged in", not "is an admin" — defense in depth on a route
 * that can delete other users' data.
 */
@WebServlet("/api/v1/admin/*")
public class AdminServlet extends HttpServlet {

    private AdminService adminService;

    @Override
    public void init() throws ServletException {
        var ds = DataSourceProvider.get(getServletContext());
        UserDAO userDAO = new UserDAOImpl(ds);
        OrderDAO orderDAO = new OrderDAOImpl(ds);
        ProductDAO productDAO = new ProductDAOImpl(ds);
        adminService = new AdminService(userDAO, orderDAO, new ProductService(productDAO));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!requireAdmin(req, resp)) return;

        String pathInfo = req.getPathInfo();
        if ("/users".equals(pathInfo)) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(adminService.listAllUsers()));
        } else if ("/orders".equals(pathInfo)) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(adminService.listAllOrders()));
        } else {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_NOT_FOUND,
                    ApiResponse.fail("NOT_FOUND", "Unknown admin resource"));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!requireAdmin(req, resp)) return;

        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.startsWith("/products/")) {
            try {
                long productId = Long.parseLong(pathInfo.substring("/products/".length()));
                adminService.removeListing(productId);
                JsonUtil.writeJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(Map.of("removed", true)));
            } catch (NumberFormatException e) {
                JsonUtil.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                        ApiResponse.fail("VALIDATION_ERROR", "Product id must be numeric"));
            } catch (NotFoundException e) {
                JsonUtil.writeJson(resp, HttpServletResponse.SC_NOT_FOUND,
                        ApiResponse.fail("NOT_FOUND", e.getMessage()));
            }
        } else {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_NOT_FOUND,
                    ApiResponse.fail("NOT_FOUND", "Unknown admin resource"));
        }
    }

    /** Returns true and lets the caller proceed if the session belongs to an ADMIN;
     *  otherwise writes the appropriate 401/403 and returns false. */
    private boolean requireAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_UNAUTHORIZED,
                    ApiResponse.fail("UNAUTHENTICATED", "Login required"));
            return false;
        }
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_FORBIDDEN,
                    ApiResponse.fail("FORBIDDEN", "Admin role required"));
            return false;
        }
        return true;
    }
}
