package comp.rest.services;

import comp.rest.bill.BillReceiptFormatter;
import comp.rest.bill.TaxCalculator;
import comp.rest.models.Bill;
import comp.rest.models.Order;
import comp.rest.repository.BillRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class BillService {

    private final BillRepository billRepository;
    private final TaxCalculator taxCalculator;

    public BillService(BillRepository billRepository) {
        this(billRepository, new TaxCalculator());
    }

    public BillService(BillRepository billRepository, TaxCalculator taxCalculator) {
        this.billRepository = billRepository;
        this.taxCalculator  = taxCalculator != null ? taxCalculator : new TaxCalculator();
    }

    public Bill generateBill(Order order) {
        return generateBill(order, 0.0);
    }

    public Bill generateBill(Order order, double discountPercent) {
        if (order == null) {
            throw new IllegalArgumentException("Cannot generate bill for null order");
        }

        double subtotal = round(order.calculateTotal());
        double discountAmount = 0.0;
        if (discountPercent > 0) {
            discountAmount = round(subtotal * (discountPercent / 100.0));
        }

        double taxableAmount = Math.max(0.0, subtotal - discountAmount);
        double taxAmount = round(taxCalculator.calculateTotalTax(taxableAmount));
        double totalAmount = round(taxableAmount + taxAmount);

        Bill bill = new Bill(order.getId(), subtotal, taxAmount, discountAmount, totalAmount);
        return billRepository.save(bill);
    }

    public Optional<Bill> getBillById(int id) {
        return billRepository.findById(id);
    }

    public Optional<Bill> getBillByOrderId(int orderId) {
        return billRepository.findByOrderId(orderId);
    }

    public List<Bill> getAllBills() {
        return billRepository.getAllBills();
    }

    public List<Bill> getUnpaidBills() {
        return billRepository.findByStatus(Bill.Status.UNPAID);
    }

    public List<Bill> getPaidBills() {
        return billRepository.findByStatus(Bill.Status.PAID);
    }

    public boolean markBillAsPaid(int billId) {
        return billRepository.updateStatus(billId, Bill.Status.PAID, LocalDateTime.now());
    }

    public boolean cancelBill(int billId) {
        return billRepository.updateStatus(billId, Bill.Status.CANCELLED, null);
    }

    public String formatReceipt(Bill bill, Order order) {
        return BillReceiptFormatter.formatReceipt(bill, order);
    }

    public TaxCalculator getTaxCalculator() {
        return taxCalculator;
    }

    private double round(double val) {
        return Math.round(val * 100.0) / 100.0;
    }
}
