package cms;

import java.util.ArrayList;
import java.util.List;

public class Invoice {

    public static class LineItem {
        private String service;
        private int quantity;
        private double unitPrice;

        public LineItem(String service, int quantity, double unitPrice) {
            this.service   = service;
            this.quantity  = quantity;
            this.unitPrice = unitPrice;
        }

        public String getService()    { return service; }
        public int    getQuantity()   { return quantity; }
        public double getUnitPrice()  { return unitPrice; }
        public double getSubtotal()   { return quantity * unitPrice; }
    }

    public enum Status { UNPAID, PARTIALLY_PAID, PAID }

    private String invoiceId;
    private String patientId;
    private String patientName;
    private String appointmentRef;
    private List<LineItem> lineItems = new ArrayList<>();
    private double discount;
    private double totalAmount;
    private Status status;
    private String paymentMethod;
    private String paymentTimestamp;
    private double amountPaid;
    private String createdAt;

    public Invoice(String invoiceId, String patientId, String patientName,
                   String appointmentRef, String createdAt) {
        this.invoiceId      = invoiceId;
        this.patientId      = patientId;
        this.patientName    = patientName;
        this.appointmentRef = appointmentRef;
        this.createdAt      = createdAt;
        this.status         = Status.UNPAID;
        this.amountPaid     = 0;
    }

    public void addLineItem(LineItem item) { lineItems.add(item); }

    public double calculateTotal() {
        double subtotal = lineItems.stream().mapToDouble(LineItem::getSubtotal).sum();
        return Math.max(0, subtotal - discount);
    }

    // Getters
    public String          getInvoiceId()      { return invoiceId; }
    public String          getPatientId()      { return patientId; }
    public String          getPatientName()    { return patientName; }
    public String          getAppointmentRef() { return appointmentRef; }
    public List<LineItem>  getLineItems()      { return lineItems; }
    public double          getDiscount()       { return discount; }
    public double          getTotalAmount()    { return totalAmount; }
    public Status          getStatus()         { return status; }
    public String          getPaymentMethod()  { return paymentMethod; }
    public String          getPaymentTimestamp(){ return paymentTimestamp; }
    public double          getAmountPaid()     { return amountPaid; }
    public String          getCreatedAt()      { return createdAt; }

    // Setters
    public void setDiscount(double d)           { this.discount = d; }
    public void setTotalAmount(double t)        { this.totalAmount = t; }
    public void setStatus(Status s)             { this.status = s; }
    public void setPaymentMethod(String m)      { this.paymentMethod = m; }
    public void setPaymentTimestamp(String ts)  { this.paymentTimestamp = ts; }
    public void setAmountPaid(double a)         { this.amountPaid = a; }

    public double getOutstandingBalance() { return totalAmount - amountPaid; }

    public String getStatusLabel() {
        switch (status) {
            case PAID:           return "Paid";
            case PARTIALLY_PAID: return "Partially Paid";
            default:             return "Unpaid";
        }
    }
}
