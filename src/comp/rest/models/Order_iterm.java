package comp.rest.models;

public class Order_iterm {

    private int id;
    private Menu_Iterm menuItem;
    private int quantity;

    public Order_iterm(int id, Menu_Iterm menuItem, int quantity) {
        this.id       = id;
        this.menuItem = menuItem;
        this.quantity = quantity;
    }

    public int getId()                    { return id; }
    public void setId(int id)             { this.id = id; }

    public Menu_Iterm getMenuItem()       { return menuItem; }
    public void setMenuItem(Menu_Iterm m) { this.menuItem = m; }

    public int getQuantity()              { return quantity; }
    public void setQuantity(int q)        { this.quantity = q; }

    public double getSubtotal() {
        return menuItem != null ? menuItem.getPrice() * quantity : 0.0;
    }

    @Override
    public String toString() {
        String itemName = menuItem != null ? menuItem.getName() : "N/A";
        return String.format("  %-20s x%d  = Rs.%.2f", itemName, quantity, getSubtotal());
    }
}
