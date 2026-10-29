package comp.rest.models;

import java.time.LocalDateTime;

public class Payment {

    public enum Method {
        CASH,
        CARD,
        UPI
    }

    public enum Status {
        PAID,
        PENDING,
        FAILED
    }

    private int id;
    private int orderId;
    private double amount;
    private Method method;
    private Status status;
    private LocalDateTime paidAt;

    public Payment(int id, int orderId, double amount, Method method, Status status, LocalDateTime paidAt) {
        this.id      = id;
        this.orderId = orderId;
        this.amount  = amount;
        this.method  = method;
        this.status  = status;
        this.paidAt  = paidAt;
    }

    public int getId()               { return id; }
    public void setId(int id)        { this.id = id; }

    public int getOrderId()          { return orderId; }
    public void setOrderId(int oid)  { this.orderId = oid; }

    public double getAmount()           { return amount; }
    public void setAmount(double amt)   { this.amount = amt; }

    public Method getMethod()           { return method; }
    public void setMethod(Method m)     { this.method = m; }

    public Status getStatus()           { return status; }
    public void setStatus(Status s)     { this.status = s; }

    public LocalDateTime getPaidAt()             { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt)  { this.paidAt = paidAt; }

    @Override
    public String toString() {
        return String.format("Payment#%d  Order#%d  Rs.%.2f  %-6s  %-8s  %s",
                id, orderId, amount, method, status, paidAt);
    }
}
