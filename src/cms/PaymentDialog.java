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
    private JTextField cardNumberField, expiryField, cvvField, cardHolderField;
    private JTextField insuranceProviderField, policyNumberField;
    private JPanel cardPanel, insurancePanel;
    private JLabel statusLabel;

    public PaymentDialog(JFrame parent) {
        this.parent = parent;
        buildDialog();
    }

    private void buildDialog() {
        dialog = new JDialog(parent, "Mark Payment as Received", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout());
        dialog.setResizable(false);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIHelper.DARK_BLUE);
        header.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        JLabel title = new JLabel("Mark Payment as Received");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Enter payment details below");
        sub.setFont(UIHelper.SMALL_FONT);
        sub.setForeground(new Color(0xAED6F1));
        header.add(title, BorderLayout.NORTH);
        header.add(sub,   BorderLayout.SOUTH);
        dialog.add(header, BorderLayout.NORTH);

        // Body
        JPanel body = new JPanel();
        body.setBackground(UIHelper.WHITE);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));

        invoiceIdLabel = boldLabel();
        patientLabel   = boldLabel();
        totalLabel     = boldLabel();
        paidLabel      = boldLabel();
        balanceLabel   = boldLabel();
        balanceLabel.setForeground(UIHelper.ERROR);

        body.add(row("Invoice ID:",          invoiceIdLabel));
        body.add(Box.createVerticalStrut(6));
        body.add(row("Patient:",             patientLabel));
        body.add(Box.createVerticalStrut(6));
        body.add(row("Invoice Total:",       totalLabel));
        body.add(Box.createVerticalStrut(6));
        body.add(row("Already Paid:",        paidLabel));
        body.add(Box.createVerticalStrut(6));
        body.add(row("Outstanding Balance:", balanceLabel));
        body.add(Box.createVerticalStrut(10));
        body.add(new JSeparator(SwingConstants.HORIZONTAL));
        body.add(Box.createVerticalStrut(12));

        amountField = UIHelper.makeField();
        amountField.setColumns(12);
        amountField.setEditable(true);
        amountField.setFocusable(true);
        methodBox   = new JComboBox<>(new String[]{"Cash", "Card", "Insurance"});
        methodBox.setFont(UIHelper.FIELD_FONT);
        methodBox.addActionListener(e -> updatePaymentPanels());

        body.add(row("Payment Amount (Rs.):", amountField));
        body.add(Box.createVerticalStrut(8));
        body.add(row("Payment Method:",       methodBox));
        body.add(Box.createVerticalStrut(10));

        cardPanel = buildCardPanel();
        cardPanel.setVisible(false);
        body.add(cardPanel);
        body.add(Box.createVerticalStrut(10));

        insurancePanel = buildInsurancePanel();
        insurancePanel.setVisible(false);
        body.add(insurancePanel);
        body.add(Box.createVerticalStrut(10));

        statusLabel = new JLabel(" ");
        statusLabel.setFont(UIHelper.SMALL_FONT);
        body.add(statusLabel);
        body.add(Box.createVerticalStrut(6));

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

        dialog.pack();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int maxHeight = screen.height - 120;
        int height = Math.min(dialog.getHeight(), maxHeight);
        dialog.setSize(520, height);
        dialog.setLocationRelativeTo(parent);
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
        p.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        JLabel lbl = UIHelper.makeLabel(labelText);
        lbl.setPreferredSize(new Dimension(170, 24));
        p.add(lbl,  BorderLayout.WEST);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildCardPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 8));
        panel.setBackground(UIHelper.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xD2E6EE), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        cardHolderField  = UIHelper.makeField();
        cardNumberField  = UIHelper.makeField();
        expiryField      = UIHelper.makeField();
        cvvField         = UIHelper.makeField();

        panel.add(UIHelper.makeLabel("Cardholder Name:"));
        panel.add(cardHolderField);
        panel.add(UIHelper.makeLabel("Card Number:"));
        panel.add(cardNumberField);
        panel.add(UIHelper.makeLabel("Expiry (MM/YY):"));
        panel.add(expiryField);
        panel.add(UIHelper.makeLabel("CVV:"));
        panel.add(cvvField);
        return panel;
    }

    private JPanel buildInsurancePanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 8));
        panel.setBackground(UIHelper.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xD2E6EE), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        insuranceProviderField = UIHelper.makeField();
        policyNumberField     = UIHelper.makeField();

        panel.add(UIHelper.makeLabel("Insurance Provider:"));
        panel.add(insuranceProviderField);
        panel.add(UIHelper.makeLabel("Policy Number:"));
        panel.add(policyNumberField);
        return panel;
    }

    private void updatePaymentPanels() {
        String method = (String) methodBox.getSelectedItem();
        boolean card = "Card".equals(method);
        boolean insurance = "Insurance".equals(method);
        cardPanel.setVisible(card);
        insurancePanel.setVisible(insurance);
        cardNumberField.setText("");
        expiryField.setText("");
        cvvField.setText("");
        cardHolderField.setText("");
        insuranceProviderField.setText("");
        policyNumberField.setText("");
        dialog.pack();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int maxHeight = screen.height - 120;
        int height = Math.min(dialog.getHeight(), maxHeight);
        dialog.setSize(480, height);
        dialog.setLocationRelativeTo(parent);
    }

    private String collectPaymentDetails(String method) {
        if ("Card".equals(method)) {
            String cardName = cardHolderField.getText().trim();
            String cardNumber = cardNumberField.getText().trim().replaceAll("\\s+", "");
            String expiry = expiryField.getText().trim();
            String cvv = cvvField.getText().trim();
            if (cardName.isEmpty() || cardNumber.isEmpty() || expiry.isEmpty() || cvv.isEmpty()) {
                statusLabel.setText("Please complete all card details.");
                statusLabel.setForeground(UIHelper.ERROR);
                return null;
            }
            if (!cardNumber.matches("\\d{13,19}")) {
                statusLabel.setText("Card number must be 13-19 digits.");
                statusLabel.setForeground(UIHelper.ERROR);
                return null;
            }
            if (!expiry.matches("(0[1-9]|1[0-2])/\\d{2}")) {
                statusLabel.setText("Expiry must be in MM/YY format.");
                statusLabel.setForeground(UIHelper.ERROR);
                return null;
            }
            if (!cvv.matches("\\d{3,4}")) {
                statusLabel.setText("CVV must be 3 or 4 digits.");
                statusLabel.setForeground(UIHelper.ERROR);
                return null;
            }
            return "Card ending " + cardNumber.substring(cardNumber.length() - 4) + " (" + cardName + ")";
        }
        if ("Insurance".equals(method)) {
            String provider = insuranceProviderField.getText().trim();
            String policy   = policyNumberField.getText().trim();
            if (provider.isEmpty() || policy.isEmpty()) {
                statusLabel.setText("Please enter insurance provider and policy number.");
                statusLabel.setForeground(UIHelper.ERROR);
                return null;
            }
            return provider + " / Policy " + policy;
        }
        return "Cash";
    }

    public void show(Invoice invoice, Runnable onSuccess) {
        invoiceIdLabel.setText(invoice.getInvoiceId());
        patientLabel.setText(invoice.getPatientName() + " (" + invoice.getPatientId() + ")");
        totalLabel.setText(String.format("Rs. %.2f", invoice.getTotalAmount()));
        paidLabel.setText(String.format("Rs. %.2f",  invoice.getAmountPaid()));
        balanceLabel.setText(String.format("Rs. %.2f", invoice.getOutstandingBalance()));
        amountField.setText("");
        methodBox.setSelectedIndex(0);
        updatePaymentPanels();
        statusLabel.setText(" ");

        // Remove old listeners, add fresh one
        for (ActionListener al : confirmBtn.getActionListeners())
            confirmBtn.removeActionListener(al);

        SwingUtilities.invokeLater(() -> amountField.requestFocusInWindow());

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
            String methodDetails = collectPaymentDetails(method);
            if (methodDetails == null) return;
            try {
                PaymentService.applyPayment(invoice, amount, method, methodDetails);
                JOptionPane.showMessageDialog(dialog,
                    "Payment recorded successfully!\n\nInvoice: " + invoice.getInvoiceId()
                        + "\nAmount Paid: Rs. " + String.format("%.2f", amount)
                        + "\nMethod: " + method
                        + "\nDetails: " + methodDetails
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
