package comp.rest.CLI;

import comp.rest.models.Bill;
import comp.rest.models.Table;
import comp.rest.services.BillService;
import comp.rest.services.TableService;
import java.util.List;
import java.util.Scanner;

public class AdminCLI {

    private static final String ADMIN_PASSWORD = "admin@123";
    private final TableService tableService;
    private final BillService billService;
    private final Scanner scanner;

    public AdminCLI(TableService tableService, Scanner scanner) {
        this(tableService, new BillService(new comp.rest.repository.BillRepository()), scanner);
    }

    public AdminCLI(TableService tableService, BillService billService, Scanner scanner) {
        this.tableService = tableService;
        this.billService  = billService;
        this.scanner      = scanner;
    }

    public void enter() {
        System.out.print("\n  Enter Admin Password: ");
        String pwd = scanner.next();
        if (!ADMIN_PASSWORD.equals(pwd)) {
            System.out.println("  ✗  Incorrect password. Access denied.\n");
            return;
        }
        System.out.println("  ✓  Access granted. Welcome, Admin!\n");
        adminMenu();
    }

    private void adminMenu() {
        while (true) {
            printAdminMenu();
            int choice = readInt();
            switch (choice) {
                case 1 -> editTableMenu();
                case 2 -> viewBillingReports();
                case 3 -> {
                    System.out.println("  Returning to main menu...\n");
                    return;
                }
                default -> System.out.println("  Invalid choice. Try again.\n");
            }
        }
    }

    private void printAdminMenu() {
        System.out.println("╔══════════════════════════════╗");
        System.out.println("║        ADMIN  PANEL          ║");
        System.out.println("╠══════════════════════════════╣");
        System.out.println("║  1. Edit Tables              ║");
        System.out.println("║  2. View Billing Reports     ║");
        System.out.println("║  3. Exit (Back to Main Menu) ║");
        System.out.println("╚══════════════════════════════╝");
        System.out.print("  Choose: ");
    }

    private void editTableMenu() {
        while (true) {
            printEditMenu();
            int choice = readInt();
            switch (choice) {
                case 1 -> removeTable();
                case 2 -> addTable();
                case 3 -> {
                    System.out.println("  Returning to admin menu...\n");
                    return;
                }
                default -> System.out.println("  Invalid choice. Try again.\n");
            }
        }
    }

    private void printEditMenu() {
        System.out.println("┌──────────────────────────────┐");
        System.out.println("│       EDIT  TABLES           │");
        System.out.println("├──────────────────────────────┤");
        System.out.println("│  1. Remove a Table           │");
        System.out.println("│  2. Add a Table              │");
        System.out.println("│  3. Exit (Back to Admin Menu)│");
        System.out.println("└──────────────────────────────┘");
        System.out.print("  Choose: ");
    }

    private void removeTable() {
        System.out.println("\n  Current Tables:");
        printAllTables();
        System.out.print("  Enter Table ID to remove: ");
        int id = readInt();
        if (tableService.removeTable(id)) {
            System.out.println("  ✓  Table #" + id + " removed successfully.\n");
        } else {
            System.out.println("  ✗  Table #" + id + " not found.\n");
        }
    }

    private void addTable() {
        System.out.print("  Enter seating capacity for new table: ");
        int capacity = readInt();
        if (capacity <= 0) {
            System.out.println("  ✗  Capacity must be positive.\n");
            return;
        }
        Table t = tableService.addTable(capacity);
        System.out.println("  ✓  Table #" + t.getId() + " (capacity " + capacity + ") added.\n");
    }

    private void printAllTables() {
        System.out.println("  ┌────────┬──────────┬─────────────┐");
        System.out.println("  │  ID    │ Capacity │ Status      │");
        System.out.println("  ├────────┼──────────┼─────────────┤");
        for (Table t : tableService.getAllTables()) {
            System.out.printf("  │  %-5d │   %-6d │ %-11s │%n",
                    t.getId(), t.getCapacity(), t.isAvailable());
        }
        System.out.println("  └────────┴──────────┴─────────────┘\n");
    }

    private void viewBillingReports() {
        List<Bill> bills = billService.getAllBills();
        System.out.println("\n  ╔═══════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("  ║                              BILLING & FINANCIAL REPORTS                          ║");
        System.out.println("  ╚═══════════════════════════════════════════════════════════════════════════════════╝");

        if (bills.isEmpty()) {
            System.out.println("  No bills have been generated yet.\n");
            return;
        }

        double totalRevenue = 0.0;
        double totalTax = 0.0;
        int paidCount = 0;
        int unpaidCount = 0;

        for (Bill b : bills) {
            if (b.isPaid()) {
                totalRevenue += b.getTotalAmount();
                totalTax += b.getTaxAmount();
                paidCount++;
            } else {
                unpaidCount++;
            }
        }

        System.out.printf("  Total Invoices : %d  |  Paid: %d  |  Unpaid: %d%n", bills.size(), paidCount, unpaidCount);
        System.out.printf("  Total Revenue  : Rs.%.2f%n", totalRevenue);
        System.out.printf("  Total Tax Coll : Rs.%.2f%n", totalTax);
        System.out.println("  ───────────────────────────────────────────────────────────────────────────────────");
        System.out.println("  ┌────────┬──────────┬───────────┬───────────┬───────────┬──────────┬──────────────────┐");
        System.out.println("  │ Bill # │ Order #  │ Subtotal  │ Tax (5%)  │ Total     │ Status   │ Date             │");
        System.out.println("  ├────────┼──────────┼───────────┼───────────┼───────────┼──────────┼──────────────────┤");
        for (Bill b : bills) {
            String dateStr = b.getCreatedAt() != null ? b.getCreatedAt().toLocalDate().toString() : "N/A";
            System.out.printf("  │ %-6d │ %-8d │ Rs.%-6.2f │ Rs.%-6.2f │ Rs.%-6.2f │ %-8s │ %-16s │%n",
                    b.getId(), b.getOrderId(), b.getSubtotal(), b.getTaxAmount(), b.getTotalAmount(), b.getStatus(), dateStr);
        }
        System.out.println("  └────────┴──────────┴───────────┴───────────┴───────────┴──────────┴──────────────────┘\n");
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
