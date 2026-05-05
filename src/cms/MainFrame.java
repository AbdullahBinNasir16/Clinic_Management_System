package cms;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private JPanel contentPanel;
    private SidebarNavigation sidebar;
    private DashboardPanel dashboardPanel;
    private ProfilePanel profilePanel;
    private RegisterPanel registerPanel;
    private SearchPanel searchPanel;
    private AppointmentPanel appointmentPanel;
    private GenerateInvoicePanel invoicePanel;
    private BillingHistoryPanel billingPanel;

    public MainFrame() {
        setTitle("Clinic Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);

        // ── Top banner ────────────────────────────────────────────────────────
        JPanel banner = UIHelper.headerPanel("Clinic Management System",
            "Modern clinic management for patients, appointments, and billing.");

        // ── Main content area with sidebar + content ──────────────────────────
        JPanel main = new JPanel(new BorderLayout(0, 0));
        main.setBackground(UIHelper.PALE_BLUE);

        // Initialize all panels
        dashboardPanel = new DashboardPanel();
        profilePanel = new ProfilePanel();
        registerPanel = new RegisterPanel();
        searchPanel = new SearchPanel(profilePanel);
        appointmentPanel = new AppointmentPanel();
        invoicePanel = new GenerateInvoicePanel();
        billingPanel = new BillingHistoryPanel();

        // Sidebar navigation
        sidebar = new SidebarNavigation();
        sidebar.addNavItem("Dashboard");
        sidebar.addNavItem("Register Patient");
        sidebar.addNavItem("Search Patient");
        sidebar.addNavItem("Patient Profile");
        sidebar.addNavItem("Appointments");
        sidebar.addNavItem("Generate Invoice");
        sidebar.addNavItem("Billing History");

        // Content area
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UIHelper.PALE_BLUE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        sidebar.setOnSelectionChanged(this::switchContent);
        sidebar.selectFirst();

        main.add(sidebar, BorderLayout.WEST);
        main.add(contentPanel, BorderLayout.CENTER);

        add(banner, BorderLayout.NORTH);
        add(main, BorderLayout.CENTER);
    }

    private void switchContent() {
        int selectedIndex = sidebar.getSelectedIndex();
        contentPanel.removeAll();

        switch (selectedIndex) {
            case 0 -> {
                dashboardPanel.refresh();
                contentPanel.add(dashboardPanel, BorderLayout.CENTER);
            }
            case 1 -> contentPanel.add(registerPanel, BorderLayout.CENTER);
            case 2 -> {
                searchPanel.refresh();
                contentPanel.add(searchPanel, BorderLayout.CENTER);
            }
            case 3 -> contentPanel.add(profilePanel, BorderLayout.CENTER);
            case 4 -> {
                appointmentPanel.refresh();
                contentPanel.add(appointmentPanel, BorderLayout.CENTER);
            }
            case 5 -> contentPanel.add(invoicePanel, BorderLayout.CENTER);
            case 6 -> {
                billingPanel.refresh();
                contentPanel.add(billingPanel, BorderLayout.CENTER);
            }
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public static void main(String[] args) {
        UIHelper.setLookAndFeel();
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}

