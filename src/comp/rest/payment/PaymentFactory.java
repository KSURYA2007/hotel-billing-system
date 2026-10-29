package comp.rest.payment;

import java.util.Scanner;

public class PaymentFactory {

    public static PaymentMethod create(int choice, Scanner scanner) {
        return switch (choice) {
            case 1 -> new CashPayment(scanner);
            case 2 -> new CardPayment(scanner);
            case 3 -> new UpiPayment(scanner);
            default -> null;
        };
    }
}
