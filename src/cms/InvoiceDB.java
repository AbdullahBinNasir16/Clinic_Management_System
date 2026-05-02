package cms;

import java.util.*;
import java.util.stream.Collectors;

public class InvoiceDB {

    private static final Map<String, Invoice> records = new LinkedHashMap<>();
    private static int counter = 0;

    static { load(); }

    // ── Persistence ──────────────────────────────────────────────────────────

    private static void load() {
        String json = JsonStore.read(JsonStore.INVOICES);
        if (json.isEmpty()) {
            seedSampleData();
            persist();
            return;
        }
        for (Map<String, String> m : JsonStore.parseArray(json)) {
            String id = m.get("invoiceId");
            if (id == null) continue;

            Invoice inv = new Invoice(id,
                m.get("patientId"), m.get("patientName"),
                m.get("appointmentRef"), m.get("createdAt"));

            // Line items stored as pipe-separated triples: svc|qty|price;svc|qty|price
            String itemsStr = m.getOrDefault("lineItems", "");
            if (!itemsStr.isEmpty()) {
                for (String entry : itemsStr.split(";")) {
                    String[] parts = entry.split("\\|", 3);
                    if (parts.length == 3) {
                        try {
                            inv.addLineItem(new Invoice.LineItem(
                                parts[0],
                                Integer.parseInt(parts[1]),
                                Double.parseDouble(parts[2])));
                        } catch (Exception ignored) {}
                    }
                }
            }

            try { inv.setDiscount(Double.parseDouble(m.getOrDefault("discount", "0"))); }
            catch (Exception ignored) {}
            try { inv.setTotalAmount(Double.parseDouble(m.getOrDefault("totalAmount", "0"))); }
            catch (Exception ignored) {}
            try { inv.setAmountPaid(Double.parseDouble(m.getOrDefault("amountPaid", "0"))); }
            catch (Exception ignored) {}
            try { inv.setStatus(Invoice.Status.valueOf(m.get("status"))); }
            catch (Exception ignored) {}
            String pm = m.get("paymentMethod");
            if (pm != null && !pm.isEmpty()) inv.setPaymentMethod(pm);
            String pts = m.get("paymentTimestamp");
            if (pts != null && !pts.isEmpty()) inv.setPaymentTimestamp(pts);

            records.put(id, inv);
            try {
                int n = Integer.parseInt(id.substring(4));
                if (n >= counter) counter = n;
            } catch (Exception ignored) {}
        }
    }

    public static void persist() {
        List<String> objs = new ArrayList<>();
        for (Invoice inv : records.values()) {
            // Encode line items as svc|qty|price;...
            StringBuilder li = new StringBuilder();
            for (Invoice.LineItem item : inv.getLineItems()) {
                if (li.length() > 0) li.append(";");
                li.append(item.getService().replace("|","").replace(";",""))
                  .append("|").append(item.getQuantity())
                  .append("|").append(item.getUnitPrice());
            }
            objs.add(JsonStore.obj(
                "invoiceId",         inv.getInvoiceId(),
                "patientId",         inv.getPatientId(),
                "patientName",       inv.getPatientName(),
                "appointmentRef",    inv.getAppointmentRef(),
                "lineItems",         li.toString(),
                "discount",          String.valueOf(inv.getDiscount()),
                "totalAmount",       String.valueOf(inv.getTotalAmount()),
                "amountPaid",        String.valueOf(inv.getAmountPaid()),
                "status",            inv.getStatus().name(),
                "paymentMethod",     inv.getPaymentMethod()    != null ? inv.getPaymentMethod()    : "",
                "paymentTimestamp",  inv.getPaymentTimestamp() != null ? inv.getPaymentTimestamp() : "",
                "createdAt",         inv.getCreatedAt()
            ));
        }
        JsonStore.write(JsonStore.INVOICES, JsonStore.array(objs));
    }

    private static void seedSampleData() {
        Invoice inv1 = new Invoice("INV-0001", "P1001", "Ali Hassan", "APT-001", PatientDB.now());
        inv1.addLineItem(new Invoice.LineItem("Consultation Fee", 1, 500));
        inv1.addLineItem(new Invoice.LineItem("Blood Test",       1, 300));
        inv1.setDiscount(0); inv1.setTotalAmount(800);
        inv1.setStatus(Invoice.Status.PAID);
        inv1.setPaymentMethod("Cash"); inv1.setPaymentTimestamp(PatientDB.now());
        inv1.setAmountPaid(800);
        records.put(inv1.getInvoiceId(), inv1);

        Invoice inv2 = new Invoice("INV-0002", "P1001", "Ali Hassan", "APT-002", PatientDB.now());
        inv2.addLineItem(new Invoice.LineItem("Consultation Fee", 1, 500));
        inv2.addLineItem(new Invoice.LineItem("X-Ray",           1, 1200));
        inv2.setDiscount(100); inv2.setTotalAmount(1600);
        inv2.setStatus(Invoice.Status.PARTIALLY_PAID); inv2.setAmountPaid(800);
        records.put(inv2.getInvoiceId(), inv2);

        Invoice inv3 = new Invoice("INV-0003", "P1002", "Fatima Khan", "APT-003", PatientDB.now());
        inv3.addLineItem(new Invoice.LineItem("Consultation Fee", 1, 500));
        inv3.setDiscount(0); inv3.setTotalAmount(500);
        inv3.setStatus(Invoice.Status.UNPAID);
        records.put(inv3.getInvoiceId(), inv3);

        counter = 3;
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────

    public static String generateInvoiceId() { return String.format("INV-%04d", ++counter); }

    public static void save(Invoice inv) {
        records.put(inv.getInvoiceId(), inv);
        persist();
    }

    public static Invoice getById(String id)              { return records.get(id); }
    public static List<Invoice> getAll()                  { return new ArrayList<>(records.values()); }

    public static List<Invoice> getByPatientId(String patientId) {
        return records.values().stream()
            .filter(inv -> inv.getPatientId().equalsIgnoreCase(patientId))
            .sorted(Comparator.comparing(Invoice::getCreatedAt).reversed())
            .collect(Collectors.toList());
    }

    public static List<Invoice> getByPatientName(String name) {
        String q = name.toLowerCase();
        return records.values().stream()
            .filter(inv -> inv.getPatientName().toLowerCase().contains(q))
            .sorted(Comparator.comparing(Invoice::getCreatedAt).reversed())
            .collect(Collectors.toList());
    }
}
