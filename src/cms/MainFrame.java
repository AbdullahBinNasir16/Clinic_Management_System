package cms;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Clinic Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 700);
        setMinimumSize(new Dimension(820, 600));
        setLocationRelativeTo(null);

        // ── Top banner ────────────────────────────────────────────────────────
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(UIHelper.DARK_BLUE);
        banner.setPreferredSize(new Dimension(0, 52));
        banner.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));

        JLabel titleLbl = new JLabel("  Clinic Management System");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLbl.setForeground(Color.WHITE);
        banner.add(titleLbl, BorderLayout.WEST);

        // ── Tab pane ──────────────────────────────────────────────────────────
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIHelper.HEADER_FONT);
        tabs.setForeground(UIHelper.DARK_BLUE);
        tabs.setBackground(UIHelper.PALE_BLUE);

        ProfilePanel         profilePanel     = new ProfilePanel();
        RegisterPanel        registerPanel    = new RegisterPanel();
        SearchPanel          searchPanel      = new SearchPanel(profilePanel);
        AppointmentPanel     appointmentPanel = new AppointmentPanel();
        GenerateInvoicePanel invoicePanel     = new GenerateInvoicePanel();
        BillingHistoryPanel  billingPanel     = new BillingHistoryPanel();

        tabs.addTab("  Register Patient   ", registerPanel);
        tabs.addTab("  Search Patient     ", searchPanel);
        tabs.addTab("  Patient Profile    ", profilePanel);
        tabs.addTab("  Appointments       ", appointmentPanel);
        tabs.addTab("  Generate Invoice   ", invoicePanel);
        tabs.addTab("  Billing History    ", billingPanel);

        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 1) searchPanel.refresh();
            if (tabs.getSelectedIndex() == 3) appointmentPanel.refresh();
            if (tabs.getSelectedIndex() == 5) billingPanel.refresh();
        });

        add(banner, BorderLayout.NORTH);
        add(tabs,   BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        UIHelper.setLookAndFeel();
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
