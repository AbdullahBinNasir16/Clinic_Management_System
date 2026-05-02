package cms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RegisterPanel extends JPanel {

    private JTextField nameField, dobField, contactField, addressField, emergencyField;
    private JComboBox<String> genderBox;
    private JTextArea conditionsArea;
    private JLabel statusLabel;

    public RegisterPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(UIHelper.PALE_BLUE);

        add(UIHelper.headerPanel("Register New Patient",
            "Fill in all required fields (*) and click Register"), BorderLayout.NORTH);

        JPanel form = buildForm();
        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UIHelper.PALE_BLUE);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildForm() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(UIHelper.PALE_BLUE);
        outer.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JPanel card = UIHelper.card("Patient Information");
        JPanel fields = new JPanel(new GridLayout(0, 1, 0, 10));
        fields.setBackground(UIHelper.WHITE);

        nameField      = UIHelper.makeField();
        dobField       = UIHelper.makeField();
        dobField.setToolTipText("Format: YYYY-MM-DD");
        contactField   = UIHelper.makeField();
        addressField   = UIHelper.makeField();
        emergencyField = UIHelper.makeField();
        genderBox      = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderBox.setFont(UIHelper.FIELD_FONT);
        conditionsArea = new JTextArea(3, 20);
        conditionsArea.setFont(UIHelper.FIELD_FONT);
        conditionsArea.setLineWrap(true);
        conditionsArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xAABBCC)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        fields.add(UIHelper.formRow("* Full Name:",        nameField));
        fields.add(UIHelper.formRow("* Date of Birth:",    dobField));
        fields.add(UIHelper.formRow("* Gender:",           genderBox));
        fields.add(UIHelper.formRow("* Contact Number:",   contactField));
        fields.add(UIHelper.formRow("* Address:",          addressField));
        fields.add(UIHelper.formRow("* Emergency Contact:",emergencyField));
        fields.add(UIHelper.formRow("Medical Conditions:", new JScrollPane(conditionsArea)));

        card.add(fields, BorderLayout.CENTER);

        // Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setBackground(UIHelper.WHITE);
        JButton clearBtn    = UIHelper.secondaryButton("Clear");
        JButton registerBtn = UIHelper.primaryButton("Register Patient");
        btnRow.add(clearBtn);
        btnRow.add(registerBtn);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(UIHelper.SMALL_FONT);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(UIHelper.WHITE);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        bottom.add(statusLabel, BorderLayout.WEST);
        bottom.add(btnRow, BorderLayout.EAST);
        card.add(bottom, BorderLayout.SOUTH);

        // Actions
        registerBtn.addActionListener(e -> register());
        clearBtn.addActionListener(e -> clearForm());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1; gbc.weighty = 1;
        outer.add(card, gbc);
        return outer;
    }

    private void register() {
        String name      = nameField.getText().trim();
        String dob       = dobField.getText().trim();
        String gender    = (String) genderBox.getSelectedItem();
        String contact   = contactField.getText().trim();
        String address   = addressField.getText().trim();
        String emergency = emergencyField.getText().trim();
        String conditions= conditionsArea.getText().trim();

        // Validation
        if (name.isEmpty() || dob.isEmpty() || contact.isEmpty()
                || address.isEmpty() || emergency.isEmpty()) {
            statusLabel.setText("⚠  Please fill in all required fields.");
            statusLabel.setForeground(UIHelper.ERROR);
            return;
        }
        if (!dob.matches("\\d{4}-\\d{2}-\\d{2}")) {
            statusLabel.setText("⚠  Date of Birth must be in YYYY-MM-DD format.");
            statusLabel.setForeground(UIHelper.ERROR);
            return;
        }
        if (!contact.matches("[0-9\\-\\+\\s]{7,15}")) {
            statusLabel.setText("⚠  Contact number is invalid.");
            statusLabel.setForeground(UIHelper.ERROR);
            return;
        }

        // Duplicate check
        if (PatientDB.exists(name, dob)) {
            int choice = JOptionPane.showConfirmDialog(this,
                "A patient with the same name and date of birth already exists.\nProceed anyway?",
                "Possible Duplicate", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice != JOptionPane.YES_OPTION) return;
        }

        String id = PatientDB.generateId();
        Patient p = new Patient(id, name, dob, gender, contact, address, emergency,
                                conditions.isEmpty() ? "None" : conditions, PatientDB.now());
        PatientDB.save(p);

        statusLabel.setText("✓  Patient registered successfully! Patient ID: " + id);
        statusLabel.setForeground(UIHelper.SUCCESS);

        JOptionPane.showMessageDialog(this,
            "Patient registered successfully!\n\nPatient ID: " + id + "\nName: " + name,
            "Registration Successful", JOptionPane.INFORMATION_MESSAGE);

        clearForm();
    }

    private void clearForm() {
        nameField.setText("");
        dobField.setText("");
        contactField.setText("");
        addressField.setText("");
        emergencyField.setText("");
        conditionsArea.setText("");
        genderBox.setSelectedIndex(0);
        statusLabel.setText(" ");
    }
}
