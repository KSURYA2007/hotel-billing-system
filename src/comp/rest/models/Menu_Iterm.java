package comp.rest.models;

public class Menu_Iterm {

    private int id;
    private String name;
    private double price;
    private String category;

    public Menu_Iterm(int id, String name, double price) {
        this(id, name, price, "General");
    }

    public Menu_Iterm(int id, String name, double price, String category) {
        this.id       = id;
        this.name     = name;
        this.price    = price;
        this.category = category;
    }

    public int getId()               { return id; }
    public void setId(int id)        { this.id = id; }

    public String getName()          { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice()         { return price; }
    public void setPrice(double p)   { this.price = p; }

    public String getCategory()              { return category; }
    public void setCategory(String category) { this.category = category; }

    @Override
    public String toString() {
        return String.format("[%d] %-20s  Rs.%-8.2f  (%s)", id, name, price, category);
    }
}
