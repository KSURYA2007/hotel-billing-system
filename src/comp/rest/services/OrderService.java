package comp.rest.services;

import comp.rest.models.Menu_Iterm;
import comp.rest.models.Order;
import comp.rest.models.Order_iterm;
import comp.rest.repository.OrderRepository;
import java.util.List;
import java.util.Optional;

public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuService menuService;

    public OrderService(OrderRepository orderRepository, MenuService menuService) {
        this.orderRepository = orderRepository;
        this.menuService     = menuService;
    }

    public Order createOrder(int tableId) {
        return orderRepository.createOrder(tableId);
    }

    public boolean addItemToOrder(Order order, int menuItemId, int quantity) {
        Optional<Menu_Iterm> itemOpt = menuService.findById(menuItemId);
        if (itemOpt.isEmpty()) return false;
        int orderItemId = orderRepository.addOrderItem(order.getId(), menuItemId, quantity);
        order.addItem(new Order_iterm(orderItemId, itemOpt.get(), quantity));
        return true;
    }

    public boolean completeOrder(int orderId) {
        return orderRepository.completeOrder(orderId);
    }

    public boolean cancelOrder(int orderId) {
        return orderRepository.cancelOrder(orderId);
    }

    public List<Order> getOrdersByTable(int tableId) {
        return orderRepository.findByTableId(tableId);
    }

    public List<Order> getAllOrders() {
        return orderRepository.getAllOrders();
    }

    public Optional<Order> findById(int id) {
        return orderRepository.findById(id);
    }
}
