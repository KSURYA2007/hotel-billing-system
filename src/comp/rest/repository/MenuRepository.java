package comp.rest.repository;

import comp.rest.config.DatabaseConnection;
import comp.rest.models.Menu_Iterm;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MenuRepository {

    public MenuRepository() {
        seedIfEmpty();
    }

    private void seedIfEmpty() {
        String countSql = "SELECT COUNT(*) FROM menu_items";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {

            if (rs.next() && rs.getInt(1) == 0) {
                addItem("Masala Dosa",          60.0,  "Breakfast");
                addItem("Idli Sambar",          40.0,  "Breakfast");
                addItem("Chicken Biryani",     200.0,  "Main Course");
                addItem("Veg Biryani",         150.0,  "Main Course");
                addItem("Paneer Butter Masala",180.0,  "Main Course");
                addItem("Roti",                 20.0,  "Bread");
                addItem("Naan",                 30.0,  "Bread");
                addItem("Mango Lassi",          60.0,  "Drinks");
                addItem("Soft Drink",           40.0,  "Drinks");
                addItem("Gulab Jamun",          50.0,  "Desserts");
            }
        } catch (SQLException e) {
            System.err.println("DB check failed: " + e.getMessage());
        }
    }

    public List<Menu_Iterm> getAllItems() {
        List<Menu_Iterm> list = new ArrayList<>();
        String sql = "SELECT id, name, price, category FROM menu_items ORDER BY id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching menu items: " + e.getMessage());
        }
        return list;
    }

    public Optional<Menu_Iterm> findById(int id) {
        String sql = "SELECT id, name, price, category FROM menu_items WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding menu item: " + e.getMessage());
        }
        return Optional.empty();
    }

    public Menu_Iterm addItem(String name, double price, String category) {
        String sql = "INSERT INTO menu_items (name, price, category) VALUES (?, ?, ?) RETURNING id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setString(3, category);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Menu_Iterm(rs.getInt("id"), name, price, category);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding menu item: " + e.getMessage());
        }
        return new Menu_Iterm(0, name, price, category);
    }

    public boolean removeById(int id) {
        String sql = "DELETE FROM menu_items WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error removing menu item: " + e.getMessage());
            return false;
        }
    }

    private Menu_Iterm mapRow(ResultSet rs) throws SQLException {
        return new Menu_Iterm(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getDouble("price"),
                rs.getString("category")
        );
    }
}
