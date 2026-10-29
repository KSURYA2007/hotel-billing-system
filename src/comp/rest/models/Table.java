package comp.rest.models;

public class Table {

    public enum Availability {
        AVAILABLE,
        UNAVAILABLE
    }

    private int id;
    private int capacity;
    private Availability isAvailable;

    public Table(int id, int capacity, Availability isAvailable) {
        this.id = id;
        this.capacity = capacity;
        this.isAvailable = isAvailable;
    }

    public int getId() { return id; }

    public int getCapacity() { return capacity; }

    public Availability isAvailable() { return isAvailable; }

    public void freeTable() { this.isAvailable = Availability.AVAILABLE; }

    public void occupyTable() { this.isAvailable = Availability.UNAVAILABLE; }
}