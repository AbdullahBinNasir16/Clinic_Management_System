package cms;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GenerateInvoicePanel extends JPanel {

    private JTextField patientIdField, patientNameField, discountField;
    private JComboBox<String> appointmentRefBox;
    private DefaultTableModel lineItemModel;
    private JTable lineItemTable;
    private JLabel totalLabel, statusLabel;

    public GenerateInvoicePanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(UIHelper.PALE_BLUE);
        add(UIHelper.headerPanel("Generate Invoice",
            "US-08 – Create itemized invoice for a completed appointment"), BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(buildForm());
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UIHelper.PALE_BLUE);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildForm() {
        JPanel outer = new JPanel();
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setBackground(UIHelper.PALE_BLUE);
        outer.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // ── Patient & Appointment card ──────────────────────────────────────
        JPanel infoCard = UIHelper.card("Patient & Appointment Details");
        JPanel infoFields = new JPanel(new GridLayout(0, 1, 0, 10));
        infoFields.setBackground(UIHelper.WHITE);

        patientIdField      = UIHelper.makeField();
        patientNameField    = UIHelper.makeField();
        appointmentRefBox   = new JComboBox<>();
        appointmentRefBox.setFont(UIHelper.FIELD_FONT);
        appointmentRefBox.setToolTipText("Lookup a patient first to populate completed appointments");

        // Auto-populate name when patient ID is entered
        patientIdField.addActionListener(e -> lookupPatient());

        JButton lookupBtn = UIHelper.secondaryButton("Lookup");
        lookupBtn.addActionListener(e -> lookupPatient());

        JPanel idRow = new JPanel(new BorderLayout(8, 0));
        idRow.setBackground(UIHelper.WHITE);
        JLabel idLbl = UIHelper.makeLabel("* Patient ID:");
        idLbl.setPreferredSize(new Dimension(160, 30));
        idRow.add(idLbl, BorderLayout.WEST);
        idRow.add(patientIdField, BorderLayout.CENTER);
        idRow.add(lookupBtn, BorderLayout.EAST);

        infoFields.add(idRow);
        infoFields.add(UIHelper.formRow("  Patient Name:", patientNameField));
        infoFields.add(UIHelper.formRow("* Appointment Ref:", appointmentRefBox));
        infoCard.add(infoFields, BorderLayout.CENTER);

        // ── Line Items card ─────────────────────────────────────────────────
        JPanel lineCard = UIHelper.card("Service Line Items");

        String[] cols = {"Service / Description", "Qty", "Unit Price (Rs.)", "Subtotal (Rs.)"};
        lineItemModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c < 3; }
        };
        lineItemTable = new JTable(lineItemModel);
        UIHelper.styleTable(lineItemTable);
        lineItemTable.getColumnModel().getColumn(1).setPreferredWidth(50);
        lineItemTable.getColumnModel().getColumn(2).setPreferredWidth(130);
        lineItemTable.getColumnModel().getColumn(3).setPreferredWidth(130);
        lineItemTable.setPreferredScrollableViewportSize(new Dimension(600, 130));

        // Auto-compute subtotal when user finishes editing a cell
        lineItemModel.addTableModelListener(e -> refreshTotal());

        JPanel lineButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        lineButtons.setBackground(UIHelper.WHITE);
        JButton addRowBtn = UIHelper.secondaryButton("+ Add Row");
        JButton delRowBtn = UIHelper.secondaryButton("− Remove Row");
        addRowBtn.addActionListener(e -> lineItemModel.addRow(new Object[]{"", 1, 0.0, 0.0}));
        delRowBtn.addActionListener(e -> {
            int sel = lineItemTable.getSelectedRow();
            if (sel >= 0) { lineItemModel.removeRow(sel); refreshTotal(); }
        });
        lineButtons.add(addRowBtn);
        lineButtons.add(delRowBtn);

        JPanel lineCenter = new JPanel(new BorderLayout());
        lineCenter.setBackground(UIHelper.WHITE);
        lineCenter.add(new JScrollPane(lineItemTable), BorderLayout.CENTER);
        lineCenter.add(lineButtons, BorderLayout.SOUTH);
        lineCard.add(lineCenter, BorderLayout.CENTER);

        // ── Totals card ─────────────────────────────────────────────────────
        JPanel totalsCard = UIHelper.card("Invoice Total");
        JPanel totalsFields = new JPanel(new GridLayout(0, 1, 0, 8));
        totalsFields.setBackground(UIHelper.WHITE);

        discountField = UIHelper.makeField();
        discountField.setText("0");
        discountField.addActionListener(e -> refreshTotal());
        discountField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent e) { refreshTotal(); }
        });

        totalLabel = new JLabel("Rs. 0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLabel.setForeground(UIHelper.DARK_BLUE);

        totalsFields.add(UIHelper.formRow("Discount (Rs.):", discountField));
        totalsFields.add(UIHelper.formRow("Total Amount:", totalLabel));
        totalsCard.add(totalsFields, BorderLayout.CENTER);

        // ── Action buttons ──────────────────────────────────────────────────
        JPanel btnPanel = new JPanel(new BorderLayout());
        btnPanel.setBackground(UIHelper.PALE_BLUE);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        statusLabel = new JLabel(" ");
        statusLabel.setFont(UIHelper.SMALL_FONT);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setBackground(UIHelper.PALE_BLUE);
        JButton clearBtn = UIHelper.secondaryButton("Clear");
        JButton saveBtn  = UIHelper.primaryButton("Save Invoice");
        clearBtn.addActionListener(e -> clearForm());
        saveBtn.addActionListener(e -> saveInvoice());
        btnRow.add(clearBtn);
        btnRow.add(saveBtn);

        btnPanel.add(statusLabel, BorderLayout.WEST);
        btnPanel.add(btnRow, BorderLayout.EAST);

        outer.add(infoCard);
        outer.add(Box.createVerticalStrut(14));
        outer.add(lineCard);
        outer.add(Box.createVerticalStrut(14));
        outer.add(totalsCard);
        outer.add(Box.createVerticalStrut(10));
        outer.add(btnPanel);

        return outer;
    }

    private void lookupPatient() {
        String id = patientIdField.getText().trim().toUpperCase();
        if (id.isEmpty()) return;
        Patient p = PatientDB.searchById(id);
        if (p != null) {
            patientNameField.setText(p.getFullName());
            patientNameField.setForeground(UIHelper.DARK_BLUE);
            // Populate appointment ref dropdown with this patient's completed appointments
            appointmentRefBox.removeAllItems();
            java.util.List<Appointment> apts = AppointmentDB.getByPatientId(id);
            for (Appointment a : apts) {
                if (a.getStatus() == Appointment.Status.COMPLETED) {
                    appointmentRefBox.addItem(a.getAppointmentId()
                        + "  (" + a.getDate() + "  " + a.getTimeSlot()
                        + "  – " + a.getDoctorName() + ")");
                }
            }
            if (appointmentRefBox.getItemCount() == 0) {
                appointmentRefBox.addItem("No completed appointments found");
            }
        } else {
            patientNameField.setText("Patient not found");
            patientNameField.setForeground(UIHelper.ERROR);
            appointmentRefBox.removeAllItems();
        }
    }

    private void refreshTotal() {
        double discount = 0;
        try { discount = Double.parseDouble(discountField.getText().trim()); }
        catch (NumberFormatException ignored) {}

        double subtotal = 0;
        // Commit current edits first
        if (lineItemTable.isEditing()) lineItemTable.getCellEditor().stopCellEditing();
        for (int r = 0; r < lineItemModel.getRowCount(); r++) {
            try {
                int    qty   = Integer.parseInt(lineItemModel.getValueAt(r, 1).toString().trim());
                double price = Double.parseDouble(lineItemModel.getValueAt(r, 2).toString().trim());
                double sub   = qty * price;
                lineItemModel.setValueAt(String.format("%.2f", sub), r, 3);
                subtotal += sub;
            } catch (Exception ignored) {
                lineItemModel.setValueAt("—", r, 3);
            }
        }
        double total = Math.max(0, subtotal - discount);
        totalLabel.setText(String.format("Rs. %.2f", total));
    }

    private void saveInvoice() {
        if (lineItemTable.isEditing()) lineItemTable.getCellEditor().stopCellEditing();

        String patientId   = patientIdField.getText().trim().toUpperCase();
        String patientName = patientNameField.getText().trim();
        // Extract just the APT-xxx ID from the combo box display string
        String apptRef = "";
        if (appointmentRefBox.getSelectedItem() != null) {
            String sel = appointmentRefBox.getSelectedItem().toString().trim();
            if (sel.startsWith("APT-")) {
                apptRef = sel.split("\\s")[0]; // take "APT-xxx"
            }
        }

        if (patientId.isEmpty() || apptRef.isEmpty()) {
            showStatus("⚠  Patient ID and Appointment Ref are required.", false);
            return;
        }
        if (patientName.isEmpty() || patientName.equals("Patient not found")) {
            showStatus("⚠  Please enter a valid Patient ID and use Lookup.", false);
            return;
        }

        List<Invoice.LineItem> items = new ArrayList<>();
        for (int r = 0; r < lineItemModel.getRowCount(); r++) {
            try {
                String svc   = lineItemModel.getValueAt(r, 0).toString().trim();
                int    qty   = Integer.parseInt(lineItemModel.getValueAt(r, 1).toString().trim());
                double price = Double.parseDouble(lineItemModel.getValueAt(r, 2).toString().trim());
                if (svc.isEmpty()) continue;
                if (price <= 0) { showStatus("⚠  Amount must be greater than zero.", false); return; }
                items.add(new Invoice.LineItem(svc, qty, price));
            } catch (Exception e) {
                showStatus("⚠  Invalid value in line items. Check Qty and Unit Price.", false);
                return;
            }
        }

        if (items.isEmpty()) {
            showStatus("⚠  At least one service item is required.", false);
            return;
        }

        double discount = 0;
        try {
            discount = Double.parseDouble(discountField.getText().trim());
            if (discount < 0) { showStatus("⚠  Discount cannot be negative.", false); return; }
        } catch (NumberFormatException e) {
            showStatus("⚠  Invalid discount value.", false);
            return;
        }

        try {
            Invoice inv = InvoiceService.createInvoice(patientId, patientName, apptRef, items, discount);
            showStatus("✓  Invoice saved successfully! Invoice ID: " + inv.getInvoiceId(), true);
            JOptionPane.showMessageDialog(this,
                "Invoice saved successfully!\n\nInvoice ID: " + inv.getInvoiceId()
                    + "\nPatient: " + patientName
                    + "\nTotal: Rs. " + String.format("%.2f", inv.getTotalAmount()),
                "Invoice Created", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
        } catch (IllegalArgumentException ex) {
            showStatus("⚠  " + ex.getMessage(), false);
        }
    }

    private void showStatus(String msg, boolean success) {
        statusLabel.setText(msg);
        statusLabel.setForeground(success ? UIHelper.SUCCESS : UIHelper.ERROR);
    }

    private void clearForm() {
        patientIdField.setText("");
        patientNameField.setText("");
        appointmentRefBox.removeAllItems();
        discountField.setText("0");
        lineItemModel.setRowCount(0);
        totalLabel.setText("Rs. 0.00");
        statusLabel.setText(" ");
    }
}
