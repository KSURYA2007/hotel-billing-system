package comp.rest.models;

import java.time.LocalDateTime;

public class Bill {

    public enum Status {
        UNPAID,
        PAID,
        CANCELLED
    }

    private int id;
    private int orderId;
    private double subtotal;
    private double taxAmount;
    private double discountAmount;
    private double totalAmount;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;

    public Bill() {
        this(0, 0, 0.0, 0.0, 0.0, 0.0, Status.UNPAID, LocalDateTime.now(), null);
    }

    public Bill(int orderId, double subtotal, double taxAmount, double discountAmount, double totalAmount) {
        this(0, orderId, subtotal, taxAmount, discountAmount, totalAmount, Status.UNPAID, LocalDateTime.now(), null);
    }

    public Bill(int id, int orderId, double subtotal, double taxAmount, double discountAmount,
                double totalAmount, Status status, LocalDateTime createdAt, LocalDateTime paidAt) {
        this.id             = id;
        this.orderId        = orderId;
        this.subtotal       = subtotal;
        this.taxAmount      = taxAmount;
        this.discountAmount = discountAmount;
        this.totalAmount    = totalAmount;
        this.status         = status != null ? status : Status.UNPAID;
        this.createdAt      = createdAt != null ? createdAt : LocalDateTime.now();
        this.paidAt         = paidAt;
    }

    public int getId()                         { return id; }
    public void setId(int id)                  { this.id = id; }

    public int getOrderId()                    { return orderId; }
    public void setOrderId(int orderId)        { this.orderId = orderId; }

    public double getSubtotal()                { return subtotal; }
    public void setSubtotal(double subtotal)   { this.subtotal = subtotal; }

    public double getTaxAmount()               { return taxAmount; }
    public void setTaxAmount(double taxAmount) { this.taxAmount = taxAmount; }

    public double getDiscountAmount()                    { return discountAmount; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }

    public double getTotalAmount()                   { return totalAmount; }
    public void setTotalAmount(double totalAmount)   { this.totalAmount = totalAmount; }

    public Status getStatus()                  { return status; }
    public void setStatus(Status status)       { this.status = status; }

    public LocalDateTime getCreatedAt()                { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt)  { this.createdAt = createdAt; }

    public LocalDateTime getPaidAt()             { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt)  { this.paidAt = paidAt; }

    public void markPaid() {
        this.status = Status.PAID;
        this.paidAt = LocalDateTime.now();
    }

    public void markCancelled() {
        this.status = Status.CANCELLED;
    }

    public boolean isPaid() {
        return this.status == Status.PAID;
    }

    @Override
    public String toString() {
        return String.format("Bill#%d  Order#%d  Subtotal:Rs.%.2f  Tax:Rs.%.2f  Disc:Rs.%.2f  Total:Rs.%.2f  [%s]  Created:%s",
                id, orderId, subtotal, taxAmount, discountAmount, totalAmount, status, createdAt);
    }
}
