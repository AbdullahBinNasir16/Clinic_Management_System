package cms;

import java.util.List;

public class InvoiceService {

    /**
     * Calculates the total for a set of line items minus discount.
     * Throws IllegalArgumentException if line items list is empty.
     */
    public static double calculateTotal(List<Invoice.LineItem> items, double discount) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("At least one service item is required.");
        }
        double subtotal = items.stream().mapToDouble(Invoice.LineItem::getSubtotal).sum();
        return Math.max(0, subtotal - discount);
    }

    /**
     * Creates and saves a new invoice. Returns the saved Invoice.
     */
    public static Invoice createInvoice(String patientId, String patientName,
                                        String appointmentRef,
                                        List<Invoice.LineItem> items,
                                        double discount) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("At least one service item is required.");
        }
        for (Invoice.LineItem item : items) {
            if (item.getUnitPrice() <= 0) {
                throw new IllegalArgumentException("Amount must be greater than zero.");
            }
        }
        if (discount < 0) {
            throw new IllegalArgumentException("Discount cannot be negative.");
        }

        String id = InvoiceDB.generateInvoiceId();
        Invoice inv = new Invoice(id, patientId, patientName, appointmentRef, PatientDB.now());
        for (Invoice.LineItem item : items) {
            inv.addLineItem(item);
        }
        inv.setDiscount(discount);
        inv.setTotalAmount(calculateTotal(items, discount));
        InvoiceDB.save(inv);
        return inv;
    }
}
