package comp.rest.repository;

import comp.rest.config.DatabaseConnection;
import comp.rest.models.Menu_Iterm;
import comp.rest.models.Order;
import comp.rest.models.Order_iterm;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderRepository {

    public Order createOrder(int tableId) {
        String sql = "INSERT INTO orders (table_id, status) VALUES (?, 'PENDING') RETURNING id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tableId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new Order(rs.getInt("id"), tableId);
            }
        } catch (SQLException e) {
            System.err.println("Error creating order: " + e.getMessage());
        }
        return new Order(0, tableId);
    }

    public int addOrderItem(int orderId, int menuItemId, int quantity) {
        String sql = "INSERT INTO order_items (order_id, menu_item_id, quantity) VALUES (?, ?, ?) RETURNING id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ps.setInt(2, menuItemId);
            ps.setInt(3, quantity);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.err.println("Error adding order item: " + e.getMessage());
        }
        return 0;
    }

    public Optional<Order> findById(int id) {
        String sql = "SELECT id, table_id, status FROM orders WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order order = new Order(id, rs.getInt("table_id"), parseStatus(rs.getString("status")));
                    loadOrderItems(conn, order);
                    return Optional.of(order);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding order: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Order> findByTableId(int tableId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT id, table_id, status FROM orders WHERE table_id = ? ORDER BY id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tableId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order(rs.getInt("id"), tableId, parseStatus(rs.getString("status")));
                    loadOrderItems(conn, order);
                    list.add(order);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding orders by table: " + e.getMessage());
        }
        return list;
    }

    public List<Order> getAllOrders() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT id, table_id, status FROM orders ORDER BY id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Order order = new Order(rs.getInt("id"), rs.getInt("table_id"), parseStatus(rs.getString("status")));
                loadOrderItems(conn, order);
                list.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all orders: " + e.getMessage());
        }
        return list;
    }

    public boolean completeOrder(int orderId) {
        return updateStatus(orderId, Order.Status.COMPLETED);
    }

    public boolean cancelOrder(int orderId) {
        return updateStatus(orderId, Order.Status.CANCELLED);
    }

    private boolean updateStatus(int orderId, Order.Status status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating order status: " + e.getMessage());
            return false;
        }
    }

    private void loadOrderItems(Connection conn, Order order) {
        String sql = "SELECT oi.id, oi.quantity, m.id AS menu_id, m.name, m.price, m.category " +
                     "FROM order_items oi " +
                     "JOIN menu_items m ON oi.menu_item_id = m.id " +
                     "WHERE oi.order_id = ? ORDER BY oi.id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, order.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Menu_Iterm menu = new Menu_Iterm(
                            rs.getInt("menu_id"),
                            rs.getString("name"),
                            rs.getDouble("price"),
                            rs.getString("category")
                    );
                    order.addItem(new Order_iterm(rs.getInt("id"), menu, rs.getInt("quantity")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading order items: " + e.getMessage());
        }
    }

    private Order.Status parseStatus(String statusStr) {
        if (statusStr == null) return Order.Status.PENDING;
        try {
            return Order.Status.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Order.Status.PENDING;
        }
    }
}
