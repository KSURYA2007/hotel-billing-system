package comp.rest.payment;

import comp.rest.models.Payment;
import java.util.Scanner;

public class CashPayment implements PaymentMethod {

    private final Scanner scanner;

    public CashPayment(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public Payment.Method getMethodType() {
        return Payment.Method.CASH;
    }

    @Override
    public boolean process(double amount) {
        System.out.println("\n  ── CASH PAYMENT ──");
        System.out.printf("  Amount Due: Rs.%.2f%n", amount);
        System.out.print("  Enter cash given by customer: Rs.");

        double tendered;
        try {
            tendered = Double.parseDouble(scanner.next().trim());
        } catch (NumberFormatException e) {
            System.out.println("  ✗ Invalid cash amount entered.");
            return false;
        }

        if (tendered < amount) {
            System.out.printf("  ✗ Insufficient cash! Received Rs.%.2f, but Rs.%.2f is required.%n", tendered, amount);
            return false;
        }

        double change = tendered - amount;
        if (change > 0) {
            System.out.printf("  ✓ Return Change: Rs.%.2f%n", change);
        }
        System.out.println("  ✓ Cash payment accepted.");
        return true;
    }
}
