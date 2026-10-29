package comp.rest.bill;

import comp.rest.models.Bill;
import comp.rest.models.Order;
import comp.rest.models.Order_iterm;
import java.time.format.DateTimeFormatter;

public class BillReceiptFormatter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String formatReceipt(Bill bill, Order order) {
        StringBuilder sb = new StringBuilder();
        String dateStr = bill.getCreatedAt() != null ? bill.getCreatedAt().format(DATE_FORMATTER) : "N/A";
        double cgst = Math.round((bill.getTaxAmount() / 2.0) * 100.0) / 100.0;
        double sgst = Math.round((bill.getTaxAmount() - cgst) * 100.0) / 100.0;

        sb.append("\n======================================================\n");
        sb.append("                  RESTAURANT TAX INVOICE              \n");
        sb.append("======================================================\n");
        sb.append(String.format(" Invoice No : BILL-%05d          Date: %s\n", bill.getId(), dateStr));
        sb.append(String.format(" Order ID   : #%-6d             Table: #%d\n", bill.getOrderId(), order != null ? order.getTableId() : 0));
        sb.append(String.format(" Status     : [%s]\n", bill.getStatus()));
        sb.append("------------------------------------------------------\n");
        sb.append(String.format(" %-24s %5s  %8s  %10s\n", "Item", "Qty", "Price", "Amount"));
        sb.append("------------------------------------------------------\n");

        if (order != null && order.getItems() != null && !order.getItems().isEmpty()) {
            for (Order_iterm oi : order.getItems()) {
                String name = (oi.getMenuItem() != null) ? oi.getMenuItem().getName() : "Item";
                if (name.length() > 24) name = name.substring(0, 21) + "...";
                double price = (oi.getMenuItem() != null) ? oi.getMenuItem().getPrice() : 0.0;
                sb.append(String.format(" %-24s %5d  Rs.%6.2f  Rs.%7.2f\n",
                        name, oi.getQuantity(), price, oi.getSubtotal()));
            }
        } else {
            sb.append("  (Itemized details not loaded)\n");
        }

        sb.append("------------------------------------------------------\n");
        sb.append(String.format(" Subtotal                                  Rs.%8.2f\n", bill.getSubtotal()));
        if (bill.getTaxAmount() > 0) {
            sb.append(String.format(" CGST (2.5%%)                              Rs.%8.2f\n", cgst));
            sb.append(String.format(" SGST (2.5%%)                              Rs.%8.2f\n", sgst));
        }
        if (bill.getDiscountAmount() > 0) {
            sb.append(String.format(" Discount Applied                         -Rs.%8.2f\n", bill.getDiscountAmount()));
        }
        sb.append("======================================================\n");
        sb.append(String.format(" GRAND TOTAL                               Rs.%8.2f\n", bill.getTotalAmount()));
        sb.append("======================================================\n");
        if (bill.isPaid() && bill.getPaidAt() != null) {
            sb.append(String.format(" Paid on: %s\n", bill.getPaidAt().format(DATE_FORMATTER)));
        }
        sb.append("              Thank you! Please visit again!          \n");
        sb.append("======================================================\n");

        return sb.toString();
    }
}
