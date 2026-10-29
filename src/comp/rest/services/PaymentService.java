package comp.rest.services;

import comp.rest.models.Payment;
import comp.rest.payment.PaymentMethod;
import comp.rest.repository.PaymentRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class PaymentService {

    private final PaymentRepository repository;

    public PaymentService(PaymentRepository repository) {
        this.repository = repository;
    }

    public Payment processPayment(int orderId, double amount, PaymentMethod paymentMethod) {
        boolean success = paymentMethod.process(amount);
        if (!success) {
            return new Payment(0, orderId, amount, paymentMethod.getMethodType(), Payment.Status.FAILED, LocalDateTime.now());
        }
        return repository.save(orderId, amount, paymentMethod.getMethodType());
    }

    public Payment processPayment(int orderId, double amount, Payment.Method method) {
        return repository.save(orderId, amount, method);
    }

    public Optional<Payment> getPaymentByOrder(int orderId) {
        return repository.findByOrderId(orderId);
    }

    public List<Payment> getAllPayments() {
        return repository.getAllPayments();
    }
}
