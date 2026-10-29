package comp.rest.CLI;

import comp.rest.models.Table;
import comp.rest.services.BillService;
import comp.rest.services.MenuService;
import comp.rest.services.OrderService;
import comp.rest.services.PaymentService;
import comp.rest.services.TableService;
import java.util.List;
import java.util.Scanner;

public class TableCLI {

    private final TableService   tableService;
    private final MenuService    menuService;
    private final OrderService   orderService;
    private final PaymentService paymentService;
    private final BillService    billService;
    private final Scanner        scanner;

    private final AdminCLI adminCLI;
    private final OrderCLI orderCLI;

    public TableCLI(TableService tableService, MenuService menuService,
                    OrderService orderService, PaymentService paymentService, Scanner scanner) {
        this(tableService, menuService, orderService, paymentService,
             new BillService(new comp.rest.repository.BillRepository()), scanner);
    }

    public TableCLI(TableService tableService, MenuService menuService,
                    OrderService orderService, PaymentService paymentService,
                    BillService billService, Scanner scanner) {
        this.tableService   = tableService;
        this.menuService    = menuService;
        this.orderService   = orderService;
        this.paymentService = paymentService;
        this.billService    = billService;
        this.scanner        = scanner;
        this.adminCLI       = new AdminCLI(tableService, billService, scanner);
        this.orderCLI       = new OrderCLI(orderService, menuService, tableService, paymentService, billService, scanner);
    }

    public void displayTables() {
        printBanner();
        while (true) {
            printMainMenu();
            int choice = readInt();
            switch (choice) {
                case 1 -> showAllTables();
                case 2 -> orderCLI.start();
                case 3 -> adminCLI.enter();
                case 4 -> {
                    System.out.println("\n  Thank you! Goodbye.\n");
                    return;
                }
                default -> System.out.println("  Invalid choice. Please try again.\n");
            }
        }
    }

    private void printBanner() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║    RESTAURANT MANAGEMENT SYSTEM        ║");
        System.out.println("╚════════════════════════════════════════╝\n");
    }

    private void printMainMenu() {
        System.out.println("════════════════════════════════════════");
        System.out.println("  MAIN MENU");
        System.out.println("════════════════════════════════════════");
        System.out.println("  1. View All Tables");
        System.out.println("  2. Place an Order");
        System.out.println("  3. Admin");
        System.out.println("  4. Exit");
        System.out.println("════════════════════════════════════════");
        System.out.print("  Choose: ");
    }

    private void showAllTables() {
        List<Table> tables = tableService.getAllTables();
        System.out.println("\n  ┌────────┬──────────┬─────────────┐");
        System.out.println("  │  ID    │ Capacity │ Status      │");
        System.out.println("  ├────────┼──────────┼─────────────┤");
        for (Table t : tables) {
            System.out.printf("  │  %-5d │   %-6d │ %-11s │%n",
                    t.getId(), t.getCapacity(), t.isAvailable());
        }
        System.out.println("  └────────┴──────────┴─────────────┘\n");
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