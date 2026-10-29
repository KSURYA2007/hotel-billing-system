package comp.rest.payment;

import comp.rest.models.Payment;
import java.util.Scanner;

public class CardPayment implements PaymentMethod {

    private final Scanner scanner;

    public CardPayment(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public Payment.Method getMethodType() {
        return Payment.Method.CARD;
    }

    @Override
    public boolean process(double amount) {
        System.out.println("\n  ── CARD PAYMENT ──");
        System.out.printf("  Amount Due: Rs.%.2f%n", amount);
        System.out.print("  Enter Card Number (16 digits): ");
        String cardNum = scanner.next().trim();

        if (cardNum.length() < 12 || !cardNum.matches("\\d+")) {
            System.out.println("  ✗ Invalid card number.");
            return false;
        }

        System.out.print("  Enter Expiry (MM/YY): ");
        String expiry = scanner.next().trim();
        if (!expiry.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            System.out.println("  ✗ Invalid expiry format. Use MM/YY.");
            return false;
        }

        System.out.print("  Enter CVV (3 digits): ");
        String cvv = scanner.next().trim();
        if (!cvv.matches("\\d{3}")) {
            System.out.println("  ✗ Invalid CVV.");
            return false;
        }

        String masked = "****-****-****-" + cardNum.substring(cardNum.length() - 4);
        System.out.println("  Authorizing card " + masked + "...");
        System.out.println("  ✓ Card transaction debited successfully.");
        return true;
    }
}
