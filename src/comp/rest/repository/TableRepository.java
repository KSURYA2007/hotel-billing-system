package comp.rest.repository;

import comp.rest.config.DatabaseConnection;
import comp.rest.models.Table;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TableRepository {

    public TableRepository() {
        seedIfEmpty();
    }

    private void seedIfEmpty() {
        String countSql = "SELECT COUNT(*) FROM tables";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {

            if (rs.next() && rs.getInt(1) == 0) {
                addTable(2);
                addTable(4);
                addTable(4);
                addTable(6);
                addTable(8);
            }
        } catch (SQLException e) {
            System.err.println("DB check failed: " + e.getMessage());
        }
    }

    public List<Table> getTables() {
        List<Table> list = new ArrayList<>();
        String sql = "SELECT id, capacity, is_available FROM tables ORDER BY id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching tables: " + e.getMessage());
        }
        return list;
    }

    public Optional<Table> findById(int id) {
        String sql = "SELECT id, capacity, is_available FROM tables WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding table: " + e.getMessage());
        }
        return Optional.empty();
    }

    public Table addTable(int capacity) {
        String sql = "INSERT INTO tables (capacity, is_available) VALUES (?, 'AVAILABLE') RETURNING id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, capacity);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Table(rs.getInt("id"), capacity, Table.Availability.AVAILABLE);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding table: " + e.getMessage());
        }
        return new Table(0, capacity, Table.Availability.AVAILABLE);
    }

    public boolean removeById(int id) {
        String sql = "DELETE FROM tables WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error removing table: " + e.getMessage());
            return false;
        }
    }

    public boolean updateAvailability(int id, Table.Availability availability) {
        String sql = "UPDATE tables SET is_available = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, availability.name());
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating table availability: " + e.getMessage());
            return false;
        }
    }

    private Table mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int cap = rs.getInt("capacity");
        Table.Availability avail = "UNAVAILABLE".equalsIgnoreCase(rs.getString("is_available"))
                ? Table.Availability.UNAVAILABLE
                : Table.Availability.AVAILABLE;
        return new Table(id, cap, avail);
    }
}