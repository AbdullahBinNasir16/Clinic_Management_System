package cms;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class SearchPanel extends JPanel {

    private JTextField searchField;
    private JComboBox<String> searchType;
    private DefaultTableModel tableModel;
    private JTable resultTable;
    private JLabel statusLabel;
    private ProfilePanel profilePanel;

    public SearchPanel(ProfilePanel profilePanel) {
        this.profilePanel = profilePanel;
        setLayout(new BorderLayout(0, 0));
        setBackground(UIHelper.PALE_BLUE);

        add(UIHelper.headerPanel("Search Patient",
            "Search by Patient ID or by name"), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
    }

    private JPanel buildContent() {
        JPanel outer = new JPanel(new BorderLayout(0, 16));
        outer.setBackground(UIHelper.PALE_BLUE);
        outer.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Search bar card
        JPanel searchCard = UIHelper.card("Search");
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchRow.setBackground(UIHelper.WHITE);

        searchType  = new JComboBox<>(new String[]{"By Name", "By Patient ID"});
        searchType.setFont(UIHelper.FIELD_FONT);
        searchField = UIHelper.makeField();
        searchField.setPreferredSize(new Dimension(280, 32));
        JButton searchBtn = UIHelper.primaryButton("Search");
        JButton allBtn    = UIHelper.secondaryButton("Show All");
        statusLabel       = new JLabel(" ");
        statusLabel.setFont(UIHelper.SMALL_FONT);

        searchRow.add(searchType);
        searchRow.add(searchField);
        searchRow.add(searchBtn);
        searchRow.add(allBtn);
        searchRow.add(statusLabel);
        searchCard.add(searchRow, BorderLayout.CENTER);
        outer.add(searchCard, BorderLayout.NORTH);

        // Results card
        JPanel resultsCard = UIHelper.card("Results");
        String[] cols = {"Patient ID", "Full Name", "Date of Birth", "Gender", "Contact"};
        tableModel   = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        resultTable = new JTable(tableModel);
        UIHelper.styleTable(resultTable);
        resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(resultTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xD0E8F5)));
        resultsCard.add(scroll, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRow.setBackground(UIHelper.WHITE);
        JButton viewBtn = UIHelper.primaryButton("View Profile");
        btnRow.add(viewBtn);
        resultsCard.add(btnRow, BorderLayout.SOUTH);
        outer.add(resultsCard, BorderLayout.CENTER);

        // Actions
        searchBtn.addActionListener(e -> doSearch());
        allBtn.addActionListener(e -> loadAll());
        searchField.addActionListener(e -> doSearch());
        viewBtn.addActionListener(e -> viewSelected());

        loadAll();
        return outer;
    }

    private void doSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) { loadAll(); return; }

        tableModel.setRowCount(0);
        String type = (String) searchType.getSelectedItem();

        if ("By Patient ID".equals(type)) {
            Patient p = PatientDB.searchById(query);
            if (p != null) {
                addRow(p);
                statusLabel.setText("1 record found.");
            } else {
                statusLabel.setText("No patient found with ID: " + query);
            }
        } else {
            List<Patient> results = PatientDB.searchByName(query);
            results.forEach(this::addRow);
            statusLabel.setText(results.isEmpty()
                ? "No patients found matching: " + query
                : results.size() + " record(s) found.");
        }
        statusLabel.setForeground(UIHelper.DARK_BLUE);
    }

    private void loadAll() {
        tableModel.setRowCount(0);
        PatientDB.getAll().forEach(this::addRow);
        statusLabel.setText(PatientDB.getAll().size() + " total patients.");
        statusLabel.setForeground(UIHelper.DARK_BLUE);
    }

    private void addRow(Patient p) {
        tableModel.addRow(new Object[]{
            p.getPatientId(), p.getFullName(), p.getDob(), p.getGender(), p.getContactNumber()
        });
    }

    private void viewSelected() {
        int row = resultTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a patient from the list.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        Patient p = PatientDB.searchById(id);
        if (p != null) {
            profilePanel.loadPatient(p);
        }
    }

    public void refresh() { loadAll(); }
}
