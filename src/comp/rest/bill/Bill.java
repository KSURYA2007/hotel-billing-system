package comp.rest.bill;

import java.time.LocalDateTime;

/**
 * Convenience alias / subclass for the core Bill domain model under comp.rest.models.
 * Ensures compatibility whether referenced from comp.rest.models.Bill or comp.rest.bill.Bill.
 */
public class Bill extends comp.rest.models.Bill {

    public Bill() {
        super();
    }

    public Bill(int orderId, double subtotal, double taxAmount, double discountAmount, double totalAmount) {
        super(orderId, subtotal, taxAmount, discountAmount, totalAmount);
    }

    public Bill(int id, int orderId, double subtotal, double taxAmount, double discountAmount,
                double totalAmount, Status status, LocalDateTime createdAt, LocalDateTime paidAt) {
        super(id, orderId, subtotal, taxAmount, discountAmount, totalAmount, status, createdAt, paidAt);
    }
}
