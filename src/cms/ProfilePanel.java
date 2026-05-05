package cms;

import javax.swing.*;
import java.awt.*;

public class ProfilePanel extends JPanel {

    private Patient current;
    private JLabel idLabel, nameLabel, dobLabel, genderLabel, lastUpdatedLabel;
    private JTextField contactField, addressField, emergencyField;
    private JTextArea conditionsArea;
    private JButton editBtn, saveBtn, cancelBtn;
    private JLabel statusLabel;
    private JPanel noPatientPanel;
    private JPanel profileContent;

    public ProfilePanel() {
        setLayout(new BorderLayout());
        setBackground(UIHelper.PALE_BLUE);

        add(UIHelper.headerPanel("Patient Profile",
            "View and update patient information"), BorderLayout.NORTH);

        // No patient selected placeholder
        noPatientPanel = new JPanel(new GridBagLayout());
        noPatientPanel.setBackground(UIHelper.PALE_BLUE);
        JLabel hint = new JLabel("Search for a patient and click 'View Profile'");
        hint.setFont(UIHelper.HEADER_FONT);
        hint.setForeground(new Color(0x888888));
        noPatientPanel.add(hint);

        profileContent = buildProfileContent();
        profileContent.setVisible(false);

        JPanel center = new JPanel(new CardLayout());
        center.setBackground(UIHelper.PALE_BLUE);
        center.add(noPatientPanel, "empty");
        center.add(profileContent, "profile");

        add(center, BorderLayout.CENTER);
    }

    private JPanel buildProfileContent() {
        JPanel outer = new JPanel(new BorderLayout(0, 16));
        outer.setBackground(UIHelper.PALE_BLUE);
        outer.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JPanel card = UIHelper.card("Patient Details");
        JPanel grid = new JPanel();
        grid.setLayout(new BoxLayout(grid, BoxLayout.Y_AXIS));
        grid.setBackground(UIHelper.WHITE);

        idLabel          = infoLabel();
        nameLabel        = infoLabel();
        dobLabel         = infoLabel();
        genderLabel      = infoLabel();
        lastUpdatedLabel = infoLabel();

        contactField   = UIHelper.makeField();
        addressField   = UIHelper.makeField();
        emergencyField = UIHelper.makeField();
        conditionsArea = UIHelper.makeTextArea(4, 20);
        conditionsArea.setEditable(false);

        grid.add(UIHelper.formRow("Patient ID:",         idLabel));
        grid.add(Box.createVerticalStrut(10));
        grid.add(UIHelper.formRow("Full Name:",          nameLabel));
        grid.add(Box.createVerticalStrut(10));
        grid.add(UIHelper.formRow("Date of Birth:",      dobLabel));
        grid.add(Box.createVerticalStrut(10));
        grid.add(UIHelper.formRow("Gender:",             genderLabel));
        grid.add(Box.createVerticalStrut(10));
        grid.add(UIHelper.formRow("Contact Number:",     contactField));
        grid.add(Box.createVerticalStrut(10));
        grid.add(UIHelper.formRow("Address:",            addressField));
        grid.add(Box.createVerticalStrut(10));
        grid.add(UIHelper.formRow("Emergency Contact:",  emergencyField));
        grid.add(Box.createVerticalStrut(10));
        JScrollPane condScroll = new JScrollPane(conditionsArea);
        condScroll.setPreferredSize(new Dimension(0, 100));
        grid.add(UIHelper.formRow("Medical Conditions:", condScroll));
        grid.add(Box.createVerticalStrut(10));
        grid.add(UIHelper.formRow("Last Updated:",       lastUpdatedLabel));

        card.add(grid, BorderLayout.CENTER);

        // Buttons
        editBtn   = UIHelper.primaryButton("Edit");
        saveBtn   = UIHelper.primaryButton("Save Changes");
        cancelBtn = UIHelper.secondaryButton("Cancel");
        statusLabel = new JLabel(" ");
        statusLabel.setFont(UIHelper.SMALL_FONT);

        saveBtn.setVisible(false);
        cancelBtn.setVisible(false);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setBackground(UIHelper.WHITE);
        btnRow.add(statusLabel);
        btnRow.add(cancelBtn);
        btnRow.add(editBtn);
        btnRow.add(saveBtn);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(UIHelper.WHITE);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        bottom.add(btnRow, BorderLayout.EAST);
        card.add(bottom, BorderLayout.SOUTH);

        editBtn.addActionListener(e -> enterEditMode());
        saveBtn.addActionListener(e -> saveChanges());
        cancelBtn.addActionListener(e -> cancelEdit());

        outer.add(card, BorderLayout.CENTER);
        setEditable(false);
        return outer;
    }

    private JLabel infoLabel() {
        JLabel l = new JLabel("-");
        l.setFont(UIHelper.FIELD_FONT);
        l.setForeground(Color.DARK_GRAY);
        return l;
    }

    public void loadPatient(Patient p) {
        this.current = p;
        idLabel.setText(p.getPatientId());
        nameLabel.setText(p.getFullName());
        dobLabel.setText(p.getDob());
        genderLabel.setText(p.getGender());
        contactField.setText(p.getContactNumber());
        addressField.setText(p.getAddress());
        emergencyField.setText(p.getEmergencyContact());
        conditionsArea.setText(p.getMedicalConditions());
        lastUpdatedLabel.setText(p.getLastUpdated());
        statusLabel.setText(" ");
        setEditable(false);
        editBtn.setVisible(true);
        saveBtn.setVisible(false);
        cancelBtn.setVisible(false);
        profileContent.setVisible(true);
        noPatientPanel.setVisible(false);
        repaint(); revalidate();

        // Switch parent card layout
        Container parent = getParent();
        if (parent != null) {
            // notify main frame to switch tab if needed
        }
    }

    private void enterEditMode() {
        setEditable(true);
        editBtn.setVisible(false);
        saveBtn.setVisible(true);
        cancelBtn.setVisible(true);
        statusLabel.setText("Editing mode – make your changes then save.");
        statusLabel.setForeground(UIHelper.MED_BLUE);
    }

    private void saveChanges() {
        if (current == null) return;
        String contact   = contactField.getText().trim();
        String address   = addressField.getText().trim();
        String emergency = emergencyField.getText().trim();

        if (contact.isEmpty() || address.isEmpty() || emergency.isEmpty()) {
            statusLabel.setText("⚠  Fields cannot be empty.");
            statusLabel.setForeground(UIHelper.ERROR);
            return;
        }

        // Check if anything changed
        if (contact.equals(current.getContactNumber()) &&
            address.equals(current.getAddress()) &&
            emergency.equals(current.getEmergencyContact()) &&
            conditionsArea.getText().trim().equals(current.getMedicalConditions())) {
            statusLabel.setText("No changes detected.");
            statusLabel.setForeground(UIHelper.DARK_BLUE);
            setEditable(false);
            editBtn.setVisible(true);
            saveBtn.setVisible(false);
            cancelBtn.setVisible(false);
            return;
        }

        current.setContactNumber(contact);
        current.setAddress(address);
        current.setEmergencyContact(emergency);
        current.setMedicalConditions(conditionsArea.getText().trim());
        current.setLastUpdated(PatientDB.now());
        PatientDB.save(current);
        lastUpdatedLabel.setText(current.getLastUpdated());

        setEditable(false);
        editBtn.setVisible(true);
        saveBtn.setVisible(false);
        cancelBtn.setVisible(false);
        statusLabel.setText("✓  Patient record updated successfully.");
        statusLabel.setForeground(UIHelper.SUCCESS);

        JOptionPane.showMessageDialog(this,
            "Patient record updated successfully!",
            "Update Successful", JOptionPane.INFORMATION_MESSAGE);
    }

    private void cancelEdit() {
        loadPatient(current); // reload original
    }

    private void setEditable(boolean editable) {
        contactField.setEditable(editable);
        addressField.setEditable(editable);
        emergencyField.setEditable(editable);
        conditionsArea.setEditable(editable);
        Color bg = editable ? UIHelper.WHITE : new Color(0xF5F5F5);
        contactField.setBackground(bg);
        addressField.setBackground(bg);
        emergencyField.setBackground(bg);
        conditionsArea.setBackground(bg);
    }
}
