package comp.rest.payment;

import comp.rest.models.Payment;
import java.util.Scanner;

public class UpiPayment implements PaymentMethod {

    private final Scanner scanner;

    public UpiPayment(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public Payment.Method getMethodType() {
        return Payment.Method.UPI;
    }

    @Override
    public boolean process(double amount) {
        System.out.println("\n  ── UPI PAYMENT ──");
        System.out.printf("  Amount Due: Rs.%.2f%n", amount);
        System.out.print("  Enter UPI ID (e.g. name@okhdfcbank): ");
        String upiId = scanner.next().trim();

        if (!upiId.contains("@") || upiId.length() < 5) {
            System.out.println("  ✗ Invalid UPI ID format.");
            return false;
        }

        System.out.printf("  Requesting Rs.%.2f from %s...%n", amount, upiId);
        System.out.println("  Verifying with UPI gateway...");
        System.out.println("  ✓ UPI transaction approved.");
        return true;
    }
}
