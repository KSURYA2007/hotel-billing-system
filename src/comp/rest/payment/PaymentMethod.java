package comp.rest.payment;

import comp.rest.models.Payment;

public interface PaymentMethod {

    Payment.Method getMethodType();

    boolean process(double amount);
}
