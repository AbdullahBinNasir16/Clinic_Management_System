package cms;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class BillingHistoryPanel extends JPanel {

    private JTextField searchField;
    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel statusLabel;
    private PaymentDialog paymentDialog;

    public BillingHistoryPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(UIHelper.PALE_BLUE);
        add(UIHelper.headerPanel("Billing History",
            "US-09 – View all invoices for a patient by ID or name"), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
    }

    private JPanel buildContent() {
        JPanel outer = new JPanel(new BorderLayout(0, 14));
        outer.setBackground(UIHelper.PALE_BLUE);
        outer.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // ── Search bar ──────────────────────────────────────────────────────
        JPanel searchCard = UIHelper.card("Search Patient Billing Records");
        JPanel searchRow  = new JPanel(new BorderLayout(10, 0));
        searchRow.setBackground(UIHelper.WHITE);
        searchField = UIHelper.makeField();
        searchField.setToolTipText("Enter Patient ID (e.g. P1001) or patient name");
        searchField.addActionListener(e -> doSearch());
        JButton searchBtn = UIHelper.primaryButton("Search");
        JButton showAllBtn = UIHelper.secondaryButton("Show All");
        searchBtn.addActionListener(e -> doSearch());
        showAllBtn.addActionListener(e -> loadAll());
        JPanel btnGrp = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btnGrp.setBackground(UIHelper.WHITE);
        btnGrp.add(searchBtn);
        btnGrp.add(showAllBtn);
        searchRow.add(UIHelper.makeLabel("Patient ID / Name:"), BorderLayout.WEST);
        searchRow.add(searchField, BorderLayout.CENTER);
        searchRow.add(btnGrp, BorderLayout.EAST);
        searchCard.add(searchRow, BorderLayout.CENTER);

        // ── Results table ───────────────────────────────────────────────────
        JPanel tableCard = UIHelper.card("Invoice Records");
        String[] cols = {"Invoice ID", "Patient ID", "Patient Name", "Date", "Total (Rs.)", "Paid (Rs.)", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UIHelper.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Colour-code status column
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                String s = val == null ? "" : val.toString();
                if      (s.equals("Paid"))           setForeground(UIHelper.SUCCESS);
                else if (s.equals("Partially Paid")) setForeground(new Color(0xD4A000));
                else                                  setForeground(UIHelper.ERROR);
                if (sel) setBackground(UIHelper.LIGHT_BLUE); else setBackground(UIHelper.WHITE);
                return this;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(700, 260));

        // Action row below table
        statusLabel = new JLabel(" ");
        statusLabel.setFont(UIHelper.SMALL_FONT);

        JButton payBtn = UIHelper.primaryButton("Mark Payment Received");
        payBtn.addActionListener(e -> openPaymentDialog());

        JPanel actionRow = new JPanel(new BorderLayout());
        actionRow.setBackground(UIHelper.WHITE);
        actionRow.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        actionRow.add(statusLabel, BorderLayout.WEST);
        actionRow.add(payBtn, BorderLayout.EAST);

        JPanel tableInner = new JPanel(new BorderLayout(0, 8));
        tableInner.setBackground(UIHelper.WHITE);
        tableInner.add(scrollPane, BorderLayout.CENTER);
        tableInner.add(actionRow, BorderLayout.SOUTH);
        tableCard.add(tableInner, BorderLayout.CENTER);

        outer.add(searchCard, BorderLayout.NORTH);
        outer.add(tableCard, BorderLayout.CENTER);
        return outer;
    }

    private void doSearch() {
        String q = searchField.getText().trim();
        if (q.isEmpty()) { loadAll(); return; }
        List<Invoice> results;
        if (q.toUpperCase().startsWith("P") && q.length() <= 6) {
            results = InvoiceDB.getByPatientId(q);
            if (results.isEmpty()) results = InvoiceDB.getByPatientName(q);
        } else {
            results = InvoiceDB.getByPatientName(q);
        }
        populateTable(results);
        if (results.isEmpty()) {
            statusLabel.setText("No billing records found for this patient.");
            statusLabel.setForeground(UIHelper.ERROR);
        } else {
            statusLabel.setText(results.size() + " record(s) found.");
            statusLabel.setForeground(UIHelper.SUCCESS);
        }
    }

    private void loadAll() {
        populateTable(InvoiceDB.getAll());
        statusLabel.setText("Showing all invoices.");
        statusLabel.setForeground(UIHelper.DARK_BLUE);
    }

    private void populateTable(List<Invoice> invoices) {
        tableModel.setRowCount(0);
        for (Invoice inv : invoices) {
            tableModel.addRow(new Object[]{
                inv.getInvoiceId(),
                inv.getPatientId(),
                inv.getPatientName(),
                inv.getCreatedAt().substring(0, 10),
                String.format("%.2f", inv.getTotalAmount()),
                String.format("%.2f", inv.getAmountPaid()),
                inv.getStatusLabel()
            });
        }
    }

    private void openPaymentDialog() {
        int sel = table.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Please select an invoice from the table.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String invoiceId = tableModel.getValueAt(sel, 0).toString();
        Invoice inv = InvoiceDB.getById(invoiceId);
        if (inv == null) return;
        if (inv.getStatus() == Invoice.Status.PAID) {
            JOptionPane.showMessageDialog(this, "This invoice is already fully paid.",
                "Already Paid", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (paymentDialog == null)
            paymentDialog = new PaymentDialog((JFrame) SwingUtilities.getWindowAncestor(this));
        paymentDialog.show(inv, () -> {
            doSearch();
            statusLabel.setText("✓  Payment recorded for " + invoiceId);
            statusLabel.setForeground(UIHelper.SUCCESS);
        });
    }

    /** Called when the tab is selected so the table stays fresh. */
    public void refresh() { loadAll(); }
}
