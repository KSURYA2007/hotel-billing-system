package comp.rest;

import comp.rest.CLI.TableCLI;
import comp.rest.config.DatabaseConnection;
import comp.rest.repository.MenuRepository;
import comp.rest.repository.OrderRepository;
import comp.rest.repository.PaymentRepository;
import comp.rest.repository.TableRepository;
import comp.rest.services.MenuService;
import comp.rest.services.OrderService;
import comp.rest.services.PaymentService;
import comp.rest.services.TableService;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("Connecting to PostgreSQL (restaurant_db)...");
        if (DatabaseConnection.testConnection()) {
            System.out.println("  ✓ Database connected successfully!\n");
        } else {
            System.out.println("  ✗ Warning: Unable to connect to PostgreSQL.");
            System.out.println("    Please verify username/password in 'db.properties' file.\n");
        }

        TableRepository   tableRepo   = new TableRepository();
        MenuRepository    menuRepo    = new MenuRepository();
        OrderRepository   orderRepo   = new OrderRepository();
        PaymentRepository paymentRepo = new PaymentRepository();
        comp.rest.repository.BillRepository billRepo = new comp.rest.repository.BillRepository();

        TableService   tableService   = new TableService(tableRepo);
        MenuService    menuService    = new MenuService(menuRepo);
        OrderService   orderService   = new OrderService(orderRepo, menuService);
        PaymentService paymentService = new PaymentService(paymentRepo);
        comp.rest.services.BillService billService = new comp.rest.services.BillService(billRepo);

        Scanner scanner = new Scanner(System.in);

        TableCLI cli = new TableCLI(tableService, menuService, orderService, paymentService, billService, scanner);
        cli.displayTables();

        scanner.close();
    }
}