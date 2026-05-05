package cms;

public class PaymentService {

    /**
     * Applies a payment to an invoice.
     * Updates status to Paid, Partially Paid, etc.
     * Throws IllegalArgumentException for invalid inputs.
     */
    public static void applyPayment(Invoice invoice, double amount, String method, String details) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero.");
        }
        double outstanding = invoice.getOutstandingBalance();
        if (amount > outstanding) {
            throw new IllegalArgumentException("Payment amount exceeds outstanding balance.");
        }
        if (method == null || method.trim().isEmpty()) {
            throw new IllegalArgumentException("Please select a valid payment method.");
        }
        if (details == null || details.trim().isEmpty()) {
            throw new IllegalArgumentException("Please provide payment details.");
        }

        double newAmountPaid = invoice.getAmountPaid() + amount;
        invoice.setAmountPaid(newAmountPaid);
        invoice.setPaymentMethod(method);
        invoice.setPaymentDetails(details);
        invoice.setPaymentTimestamp(PatientDB.now());

        if (newAmountPaid >= invoice.getTotalAmount()) {
            invoice.setStatus(Invoice.Status.PAID);
        } else {
            invoice.setStatus(Invoice.Status.PARTIALLY_PAID);
        }

        InvoiceDB.save(invoice);
    }
}
