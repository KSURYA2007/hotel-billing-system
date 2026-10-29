package comp.rest.repository;

import comp.rest.config.DatabaseConnection;
import comp.rest.models.Payment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaymentRepository {

    public Payment save(int orderId, double amount, Payment.Method method) {
        String sql = "INSERT INTO payments (order_id, amount, method, status, paid_at) " +
                     "VALUES (?, ?, ?, 'PAID', NOW()) RETURNING id, paid_at";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ps.setDouble(2, amount);
            ps.setString(3, method.name());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    LocalDateTime paidAt = rs.getTimestamp("paid_at").toLocalDateTime();
                    return new Payment(id, orderId, amount, method, Payment.Status.PAID, paidAt);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving payment: " + e.getMessage());
        }
        return new Payment(0, orderId, amount, method, Payment.Status.FAILED, LocalDateTime.now());
    }

    public Optional<Payment> findByOrderId(int orderId) {
        String sql = "SELECT id, order_id, amount, method, status, paid_at FROM payments WHERE order_id = ? LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching payment: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Payment> getAllPayments() {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT id, order_id, amount, method, status, paid_at FROM payments ORDER BY id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Error fetching payments: " + e.getMessage());
        }
        return list;
    }

    private Payment mapRow(ResultSet rs) throws SQLException {
        int id           = rs.getInt("id");
        int orderId      = rs.getInt("order_id");
        double amount    = rs.getDouble("amount");
        Payment.Method method = parseMethod(rs.getString("method"));
        Payment.Status status = parseStatus(rs.getString("status"));
        Timestamp ts         = rs.getTimestamp("paid_at");
        LocalDateTime paidAt = ts != null ? ts.toLocalDateTime() : LocalDateTime.now();
        return new Payment(id, orderId, amount, method, status, paidAt);
    }

    private Payment.Method parseMethod(String s) {
        try { return Payment.Method.valueOf(s.toUpperCase()); }
        catch (Exception e) { return Payment.Method.CASH; }
    }

    private Payment.Status parseStatus(String s) {
        try { return Payment.Status.valueOf(s.toUpperCase()); }
        catch (Exception e) { return Payment.Status.PENDING; }
    }
}
