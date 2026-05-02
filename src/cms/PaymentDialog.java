package cms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PaymentDialog {

    private final JFrame parent;
    private JDialog dialog;
    private JButton confirmBtn;
    private JLabel invoiceIdLabel, patientLabel, totalLabel, paidLabel, balanceLabel;
    private JTextField amountField;
    private JComboBox<String> methodBox;
    private JLabel statusLabel;

    public PaymentDialog(JFrame parent) {
        this.parent = parent;
        buildDialog();
    }

    private void buildDialog() {
        dialog = new JDialog(parent, "Mark Payment as Received", true);
        dialog.setSize(480, 420);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout(0, 0));
        dialog.setResizable(false);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIHelper.DARK_BLUE);
        header.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        JLabel title = new JLabel("Mark Payment as Received");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Enter payment details below");
        sub.setFont(UIHelper.SMALL_FONT);
        sub.setForeground(new Color(0xAED6F1));
        header.add(title, BorderLayout.NORTH);
        header.add(sub,   BorderLayout.SOUTH);
        dialog.add(header, BorderLayout.NORTH);

        // Body
        JPanel body = new JPanel(new GridLayout(0, 1, 0, 10));
        body.setBackground(UIHelper.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(20, 28, 10, 28));

        invoiceIdLabel = boldLabel();
        patientLabel   = boldLabel();
        totalLabel     = boldLabel();
        paidLabel      = boldLabel();
        balanceLabel   = boldLabel();
        balanceLabel.setForeground(UIHelper.ERROR);

        body.add(row("Invoice ID:",          invoiceIdLabel));
        body.add(row("Patient:",             patientLabel));
        body.add(row("Invoice Total:",       totalLabel));
        body.add(row("Already Paid:",        paidLabel));
        body.add(row("Outstanding Balance:", balanceLabel));
        body.add(new JSeparator());

        amountField = UIHelper.makeField();
        methodBox   = new JComboBox<>(new String[]{"Cash", "Card", "Insurance"});
        methodBox.setFont(UIHelper.FIELD_FONT);

        body.add(row("Payment Amount (Rs.):", amountField));
        body.add(row("Payment Method:",       methodBox));

        statusLabel = new JLabel(" ");
        statusLabel.setFont(UIHelper.SMALL_FONT);
        body.add(statusLabel);
        dialog.add(body, BorderLayout.CENTER);

        // Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnRow.setBackground(UIHelper.PALE_BLUE);
        JButton cancelBtn = UIHelper.secondaryButton("Cancel");
        confirmBtn        = UIHelper.primaryButton("Confirm Payment");
        cancelBtn.addActionListener(e -> dialog.dispose());
        btnRow.add(cancelBtn);
        btnRow.add(confirmBtn);
        dialog.add(btnRow, BorderLayout.SOUTH);
    }

    private JLabel boldLabel() {
        JLabel l = new JLabel();
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(UIHelper.DARK_BLUE);
        return l;
    }

    private JPanel row(String labelText, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setBackground(UIHelper.WHITE);
        JLabel lbl = UIHelper.makeLabel(labelText);
        lbl.setPreferredSize(new Dimension(170, 28));
        p.add(lbl,  BorderLayout.WEST);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    public void show(Invoice invoice, Runnable onSuccess) {
        invoiceIdLabel.setText(invoice.getInvoiceId());
        patientLabel.setText(invoice.getPatientName() + " (" + invoice.getPatientId() + ")");
        totalLabel.setText(String.format("Rs. %.2f", invoice.getTotalAmount()));
        paidLabel.setText(String.format("Rs. %.2f",  invoice.getAmountPaid()));
        balanceLabel.setText(String.format("Rs. %.2f", invoice.getOutstandingBalance()));
        amountField.setText("");
        methodBox.setSelectedIndex(0);
        statusLabel.setText(" ");

        // Remove old listeners, add fresh one
        for (ActionListener al : confirmBtn.getActionListeners())
            confirmBtn.removeActionListener(al);

        confirmBtn.addActionListener(e -> {
            double amount;
            try {
                amount = Double.parseDouble(amountField.getText().trim());
            } catch (NumberFormatException ex) {
                statusLabel.setText("Please enter a valid numeric amount.");
                statusLabel.setForeground(UIHelper.ERROR);
                return;
            }
            String method = (String) methodBox.getSelectedItem();
            try {
                PaymentService.applyPayment(invoice, amount, method);
                JOptionPane.showMessageDialog(dialog,
                    "Payment recorded successfully!\n\nInvoice: " + invoice.getInvoiceId()
                        + "\nAmount Paid: Rs. " + String.format("%.2f", amount)
                        + "\nMethod: " + method
                        + "\nStatus: " + invoice.getStatusLabel()
                        + "\nTimestamp: " + invoice.getPaymentTimestamp(),
                    "Payment Confirmed", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                if (onSuccess != null) onSuccess.run();
            } catch (IllegalArgumentException ex) {
                statusLabel.setText(ex.getMessage());
                statusLabel.setForeground(UIHelper.ERROR);
            }
        });

        dialog.setVisible(true);
    }
}
