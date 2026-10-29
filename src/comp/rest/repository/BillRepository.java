package comp.rest.repository;

import comp.rest.config.DatabaseConnection;
import comp.rest.models.Bill;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BillRepository {

    public BillRepository() {
        ensureTableExists();
    }

    private void ensureTableExists() {
        String sql = "CREATE TABLE IF NOT EXISTS bills (" +
                     "id SERIAL PRIMARY KEY, " +
                     "order_id INT REFERENCES orders(id) ON DELETE CASCADE, " +
                     "subtotal NUMERIC(10,2) NOT NULL, " +
                     "tax_amount NUMERIC(10,2) NOT NULL DEFAULT 0.00, " +
                     "discount_amount NUMERIC(10,2) NOT NULL DEFAULT 0.00, " +
                     "total_amount NUMERIC(10,2) NOT NULL, " +
                     "status VARCHAR(20) NOT NULL DEFAULT 'UNPAID', " +
                     "created_at TIMESTAMP NOT NULL DEFAULT NOW(), " +
                     "paid_at TIMESTAMP" +
                     ")";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error initializing bills table: " + e.getMessage());
        }
    }

    public Bill save(Bill bill) {
        String sql = "INSERT INTO bills (order_id, subtotal, tax_amount, discount_amount, total_amount, status, created_at, paid_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id, created_at";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bill.getOrderId());
            ps.setDouble(2, bill.getSubtotal());
            ps.setDouble(3, bill.getTaxAmount());
            ps.setDouble(4, bill.getDiscountAmount());
            ps.setDouble(5, bill.getTotalAmount());
            ps.setString(6, bill.getStatus() != null ? bill.getStatus().name() : Bill.Status.UNPAID.name());

            Timestamp createdTs = bill.getCreatedAt() != null ? Timestamp.valueOf(bill.getCreatedAt()) : Timestamp.valueOf(LocalDateTime.now());
            ps.setTimestamp(7, createdTs);

            if (bill.getPaidAt() != null) {
                ps.setTimestamp(8, Timestamp.valueOf(bill.getPaidAt()));
            } else {
                ps.setNull(8, java.sql.Types.TIMESTAMP);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    bill.setId(rs.getInt("id"));
                    Timestamp ts = rs.getTimestamp("created_at");
                    if (ts != null) {
                        bill.setCreatedAt(ts.toLocalDateTime());
                    }
                    return bill;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving bill: " + e.getMessage());
        }
        return bill;
    }

    public Optional<Bill> findById(int id) {
        String sql = "SELECT id, order_id, subtotal, tax_amount, discount_amount, total_amount, status, created_at, paid_at " +
                     "FROM bills WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching bill by id: " + e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Bill> findByOrderId(int orderId) {
        String sql = "SELECT id, order_id, subtotal, tax_amount, discount_amount, total_amount, status, created_at, paid_at " +
                     "FROM bills WHERE order_id = ? ORDER BY id DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching bill by orderId: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Bill> getAllBills() {
        List<Bill> list = new ArrayList<>();
        String sql = "SELECT id, order_id, subtotal, tax_amount, discount_amount, total_amount, status, created_at, paid_at " +
                     "FROM bills ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all bills: " + e.getMessage());
        }
        return list;
    }

    public List<Bill> findByStatus(Bill.Status status) {
        List<Bill> list = new ArrayList<>();
        String sql = "SELECT id, order_id, subtotal, tax_amount, discount_amount, total_amount, status, created_at, paid_at " +
                     "FROM bills WHERE status = ? ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching bills by status: " + e.getMessage());
        }
        return list;
    }

    public boolean updateStatus(int billId, Bill.Status status, LocalDateTime paidAt) {
        String sql = "UPDATE bills SET status = ?, paid_at = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            if (paidAt != null) {
                ps.setTimestamp(2, Timestamp.valueOf(paidAt));
            } else {
                ps.setNull(2, java.sql.Types.TIMESTAMP);
            }
            ps.setInt(3, billId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating bill status: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteById(int id) {
        String sql = "DELETE FROM bills WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting bill: " + e.getMessage());
            return false;
        }
    }

    private Bill mapRow(ResultSet rs) throws SQLException {
        int id                 = rs.getInt("id");
        int orderId            = rs.getInt("order_id");
        double subtotal        = rs.getDouble("subtotal");
        double taxAmount       = rs.getDouble("tax_amount");
        double discountAmount  = rs.getDouble("discount_amount");
        double totalAmount     = rs.getDouble("total_amount");
        Bill.Status status     = parseStatus(rs.getString("status"));

        Timestamp createdTs    = rs.getTimestamp("created_at");
        LocalDateTime created  = createdTs != null ? createdTs.toLocalDateTime() : LocalDateTime.now();

        Timestamp paidTs       = rs.getTimestamp("paid_at");
        LocalDateTime paid     = paidTs != null ? paidTs.toLocalDateTime() : null;

        return new Bill(id, orderId, subtotal, taxAmount, discountAmount, totalAmount, status, created, paid);
    }

    private Bill.Status parseStatus(String str) {
        if (str == null) return Bill.Status.UNPAID;
        try {
            return Bill.Status.valueOf(str.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Bill.Status.UNPAID;
        }
    }
}
