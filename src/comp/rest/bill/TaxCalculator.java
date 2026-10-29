package comp.rest.bill;

public class TaxCalculator {

    public static final double DEFAULT_GST_RATE_PERCENT = 5.0; // 5% GST (2.5% CGST + 2.5% SGST)

    private final double gstRatePercent;

    public TaxCalculator() {
        this(DEFAULT_GST_RATE_PERCENT);
    }

    public TaxCalculator(double gstRatePercent) {
        if (gstRatePercent < 0) {
            throw new IllegalArgumentException("Tax rate cannot be negative");
        }
        this.gstRatePercent = gstRatePercent;
    }

    public double getGstRatePercent() {
        return gstRatePercent;
    }

    public double getCgstRatePercent() {
        return gstRatePercent / 2.0;
    }

    public double getSgstRatePercent() {
        return gstRatePercent / 2.0;
    }

    public double calculateTotalTax(double subtotal) {
        if (subtotal <= 0) return 0.0;
        return round(subtotal * (gstRatePercent / 100.0));
    }

    public double calculateCgst(double subtotal) {
        if (subtotal <= 0) return 0.0;
        return round(subtotal * (getCgstRatePercent() / 100.0));
    }

    public double calculateSgst(double subtotal) {
        if (subtotal <= 0) return 0.0;
        return round(subtotal * (getSgstRatePercent() / 100.0));
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
