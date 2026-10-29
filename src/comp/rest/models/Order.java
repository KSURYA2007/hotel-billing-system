package comp.rest.models;

import java.util.ArrayList;
import java.util.List;

public class Order {

    public enum Status {
        PENDING,
        COMPLETED,
        CANCELLED
    }

    private int id;
    private int tableId;
    private List<Order_iterm> items;
    private Status status;

    public Order(int id, int tableId) {
        this(id, tableId, Status.PENDING);
    }

    public Order(int id, int tableId, Status status) {
        this.id      = id;
        this.tableId = tableId;
        this.status  = status != null ? status : Status.PENDING;
        this.items   = new ArrayList<>();
    }

    public int getId()            { return id; }
    public void setId(int id)     { this.id = id; }

    public int getTableId()       { return tableId; }
    public void setTableId(int t) { this.tableId = t; }

    public Status getStatus()       { return status; }
    public void setStatus(Status s) { this.status = s; }

    public List<Order_iterm> getItems() { return items; }

    public void addItem(Order_iterm item) {
        if (item != null) items.add(item);
    }

    public boolean removeItem(Order_iterm item) {
        return items.remove(item);
    }

    public double calculateTotal() {
        double total = 0.0;
        for (Order_iterm i : items) total += i.getSubtotal();
        return total;
    }

    public void markCompleted() { this.status = Status.COMPLETED; }
    public void markCancelled() { this.status = Status.CANCELLED; }

    @Override
    public String toString() {
        return String.format("Order#%d  Table:%d  Status:%-10s  Items:%d  Total:Rs.%.2f",
                id, tableId, status, items.size(), calculateTotal());
    }
}