package comp.rest.CLI;

import comp.rest.models.Bill;
import comp.rest.models.Menu_Iterm;
import comp.rest.models.Order;
import comp.rest.models.Order_iterm;
import comp.rest.models.Payment;
import comp.rest.models.Table;
import comp.rest.payment.PaymentFactory;
import comp.rest.payment.PaymentMethod;
import comp.rest.services.BillService;
import comp.rest.services.MenuService;
import comp.rest.services.OrderService;
import comp.rest.services.PaymentService;
import comp.rest.services.TableService;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class OrderCLI {

    private final OrderService   orderService;
    private final MenuService    menuService;
    private final TableService   tableService;
    private final PaymentService paymentService;
    private final comp.rest.services.BillService billService;
    private final Scanner        scanner;

    public OrderCLI(OrderService orderService, MenuService menuService,
                    TableService tableService, PaymentService paymentService, Scanner scanner) {
        this(orderService, menuService, tableService, paymentService,
             new comp.rest.services.BillService(new comp.rest.repository.BillRepository()), scanner);
    }

    public OrderCLI(OrderService orderService, MenuService menuService,
                    TableService tableService, PaymentService paymentService,
                    comp.rest.services.BillService billService, Scanner scanner) {
        this.orderService   = orderService;
        this.menuService    = menuService;
        this.tableService   = tableService;
        this.paymentService = paymentService;
        this.billService    = billService;
        this.scanner        = scanner;
    }

    public void start() {
        System.out.print("  Enter Table ID to place order: ");
        int tableId = readInt();
        Optional<Table> optTable = tableService.findById(tableId);
        if (optTable.isEmpty()) {
            System.out.println("  ✗  Table #" + tableId + " does not exist.\n");
            return;
        }

        Table table = optTable.get();
        if (table.isAvailable() == Table.Availability.UNAVAILABLE) {
            System.out.println("  ✗  Table #" + tableId + " is currently occupied/UNAVAILABLE.\n");
            return;
        }

        System.out.print("  Enter number of guests (Table capacity: " + table.getCapacity() + "): ");
        int guests = readInt();
        if (guests <= 0) {
            System.out.println("  ✗  Number of guests must be at least 1.\n");
            return;
        }
        if (guests > table.getCapacity()) {
            System.out.printf("  ✗  Capacity exceeded! Table #%d only seats %d guests (Requested: %d).%n%n",
                    tableId, table.getCapacity(), guests);
            return;
        }

        tableService.markUnavailable(tableId);
        System.out.printf("  ✓  Table #%d is now occupied (%d/%d seats) and marked UNAVAILABLE.%n",
                tableId, guests, table.getCapacity());

        Order order = orderService.createOrder(tableId);
        System.out.println("  ✓  Order #" + order.getId() + " created for Table #" + tableId + "\n");

        orderLoop(order);
    }

    private void orderLoop(Order order) {
        while (true) {
            System.out.println("┌──────────────────────────────────┐");
            System.out.println("│           ORDER  MENU            │");
            System.out.println("├──────────────────────────────────┤");
            System.out.println("│  1. View Menu                    │");
            System.out.println("│  2. Add Item to Order            │");
            System.out.println("│  3. View Current Order           │");
            System.out.println("│  4. Confirm & Proceed to Payment │");
            System.out.println("│  5. Cancel Order & Exit          │");
            System.out.println("└──────────────────────────────────┘");
            System.out.print("  Choose: ");

            int choice = readInt();
            switch (choice) {
                case 1 -> printMenu();
                case 2 -> addItem(order);
                case 3 -> printCurrentOrder(order);
                case 4 -> {
                    if (order.getItems().isEmpty()) {
                        System.out.println("  ✗  Add at least one item before completing.\n");
                        break;
                    }
                    orderService.completeOrder(order.getId());
                    order.markCompleted();
                    processPayment(order);
                    return;
                }
                case 5 -> {
                    orderService.cancelOrder(order.getId());
                    order.markCancelled();
                    tableService.markAvailable(order.getTableId());
                    System.out.println("  Order cancelled. Table #" + order.getTableId() + " is now released and marked AVAILABLE.\n");
                    return;
                }
                default -> System.out.println("  Invalid choice.\n");
            }
        }
    }

    private void processPayment(Order order) {
        Bill bill = billService.generateBill(order);
        System.out.println(billService.formatReceipt(bill, order));

        System.out.println("  ╔══════════════════════════════════╗");
        System.out.println("  ║          PAYMENT                 ║");
        System.out.println("  ╠══════════════════════════════════╣");
        System.out.printf ("  ║  Bill #%-4d   Total: Rs.%-8.2f║%n", bill.getId(), bill.getTotalAmount());
        System.out.println("  ╠══════════════════════════════════╣");
        System.out.println("  ║  Select Payment Method:          ║");
        System.out.println("  ║    1. Cash                       ║");
        System.out.println("  ║    2. Card                       ║");
        System.out.println("  ║    3. UPI                        ║");
        System.out.println("  ╚══════════════════════════════════╝");
        System.out.print("  Choose: ");

        int choice = readInt();
        PaymentMethod paymentMethod = PaymentFactory.create(choice, scanner);
        if (paymentMethod == null) {
            System.out.println("  ✗ Invalid selection. Defaulting to Cash payment.");
            paymentMethod = PaymentFactory.create(1, scanner);
        }

        Payment payment = paymentService.processPayment(order.getId(), bill.getTotalAmount(), paymentMethod);

        if (payment.getStatus() == Payment.Status.PAID) {
            billService.markBillAsPaid(bill.getId());
            bill.markPaid();
            System.out.println("\n  ✓  Payment successful!");
            System.out.printf ("     Payment #%d  |  Bill #%d  |  Rs.%.2f  |  %s%n",
                    payment.getId(), bill.getId(), payment.getAmount(), payment.getMethod());
            tableService.markAvailable(order.getTableId());
            System.out.println("  ✓  Table #" + order.getTableId() + " is now released and marked AVAILABLE.\n");
        } else {
            System.out.println("  ✗  Payment failed or was cancelled. Table remains held.\n");
        }
    }

    private void printMenu() {
        List<Menu_Iterm> items = menuService.getAllItems();
        System.out.println("\n  ══ MENU ══════════════════════════════════════");
        for (Menu_Iterm item : items) {
            System.out.println("  " + item);
        }
        System.out.println("  ═════════════════════════════════════════════\n");
    }

    private void addItem(Order order) {
        printMenu();
        System.out.print("  Enter Menu Item ID: ");
        int menuId = readInt();
        System.out.print("  Enter Quantity    : ");
        int qty = readInt();
        if (qty <= 0) {
            System.out.println("  ✗  Quantity must be positive.\n");
            return;
        }
        if (orderService.addItemToOrder(order, menuId, qty)) {
            System.out.println("  ✓  Item added to order.\n");
        } else {
            System.out.println("  ✗  Menu item #" + menuId + " not found.\n");
        }
    }

    private void printCurrentOrder(Order order) {
        System.out.println("\n  ══ Order #" + order.getId() + "  (Table #" + order.getTableId() + ") ══");
        if (order.getItems().isEmpty()) {
            System.out.println("  (No items yet)");
        } else {
            for (Order_iterm item : order.getItems()) {
                System.out.println(item);
            }
        }
        System.out.printf("  ─────────────────────────────────%n");
        System.out.printf("  Total: Rs.%.2f%n%n", order.calculateTotal());
    }

    private int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.next().trim());
            } catch (NumberFormatException e) {
                System.out.print("  Please enter a valid number: ");
            }
        }
    }
}
