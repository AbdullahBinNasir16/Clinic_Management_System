package cms;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardPanel extends JPanel {

    private JLabel totalPatientsValue;
    private JLabel todayAppointmentsValue;
    private JLabel pendingInvoicesValue;
    private JLabel totalRevenueValue;
    private JPanel upcomingList;
    private AppointmentCalendar calendar;

    public DashboardPanel() {
        setBackground(UIHelper.PALE_BLUE);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(headerSection());
        add(Box.createVerticalStrut(18));
        add(statSection());
        add(Box.createVerticalStrut(24));
        add(mainDashboardSection());
        add(Box.createVerticalGlue());
    }

    private JPanel headerSection() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIHelper.PALE_BLUE);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JLabel title = new JLabel("Clinic Executive Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(UIHelper.DARK_BLUE);

        JLabel subtitle = new JLabel("Snapshot of patients, appointments, invoices and today's activity.");
        subtitle.setFont(UIHelper.SUBTITLE_FONT);
        subtitle.setForeground(new Color(0x546E7A));

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        return header;
    }

    private JPanel statSection() {
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setBackground(UIHelper.PALE_BLUE);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        totalPatientsValue = createValueLabel();
        todayAppointmentsValue = createValueLabel();
        pendingInvoicesValue = createValueLabel();
        totalRevenueValue = createValueLabel();

        statsRow.add(createStatCard("TOTAL PATIENTS", totalPatientsValue, new Color(0xFF8A65)));
        statsRow.add(createStatCard("TODAY'S APPOINTMENTS", todayAppointmentsValue, new Color(0x29B6F6)));
        statsRow.add(createStatCard("PENDING INVOICES", pendingInvoicesValue, new Color(0xAB47BC)));
        statsRow.add(createStatCard("TOTAL REVENUE", totalRevenueValue, new Color(0x26A69A)));

        return statsRow;
    }

    private JPanel mainDashboardSection() {
        JPanel split = new JPanel(new GridLayout(1, 2, 18, 0));
        split.setBackground(UIHelper.PALE_BLUE);

        JPanel leftCard = UIHelper.card("Appointment Calendar");
        leftCard.add(getCalendarPanel(), BorderLayout.CENTER);

        JPanel rightCard = UIHelper.card("Upcoming Appointments");
        rightCard.add(getUpcomingPanel(), BorderLayout.CENTER);

        split.add(leftCard);
        split.add(rightCard);
        return split;
    }

    private JLabel createValueLabel() {
        JLabel label = new JLabel("0");
        label.setFont(new Font("Segoe UI", Font.BOLD, 26));
        label.setForeground(UIHelper.WHITE);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color bgColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(new Color(0xF1F8FF));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(14));
        card.add(valueLabel);
        return card;
    }

    private JPanel getCalendarPanel() {
        if (calendar == null) {
            calendar = new AppointmentCalendar();
        }
        return calendar;
    }

    private JPanel getUpcomingPanel() {
        upcomingList = new JPanel();
        upcomingList.setBackground(UIHelper.WHITE);
        upcomingList.setLayout(new BoxLayout(upcomingList, BoxLayout.Y_AXIS));
        upcomingList.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        return upcomingList;
    }

    private void populateUpcomingAppointments() {
        upcomingList.removeAll();
        List<Appointment> nextAppointments = AppointmentDB.getAll().stream()
            .filter(a -> a.getStatus() == Appointment.Status.SCHEDULED)
            .sorted((a1, a2) -> a1.getDate().compareTo(a2.getDate()))
            .limit(6)
            .toList();

        if (nextAppointments.isEmpty()) {
            JLabel empty = new JLabel("No upcoming appointments found.");
            empty.setFont(UIHelper.FIELD_FONT);
            empty.setForeground(new Color(0x546E7A));
            upcomingList.add(empty);
        } else {
            for (Appointment apt : nextAppointments) {
                JPanel row = new JPanel(new BorderLayout());
                row.setBackground(new Color(0xF7FBFC));
                row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0xE6EEF2), 1),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)));

                JLabel title = new JLabel(apt.getPatientName() + " with " + apt.getDoctorName());
                title.setFont(new Font("Segoe UI", Font.BOLD, 12));
                title.setForeground(UIHelper.DARK_BLUE);

                JLabel detail = new JLabel(apt.getDate() + " • " + apt.getTimeSlot());
                detail.setFont(UIHelper.SMALL_FONT);
                detail.setForeground(new Color(0x546E7A));

                row.add(title, BorderLayout.NORTH);
                row.add(detail, BorderLayout.SOUTH);
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
                upcomingList.add(row);
                upcomingList.add(Box.createVerticalStrut(10));
            }
        }
        upcomingList.revalidate();
        upcomingList.repaint();
    }

    public void refresh() {
        int totalPatients = PatientDB.getAll().size();
        int todayAppointments = (int) AppointmentDB.getAll().stream()
            .filter(a -> a.getDate().equals(LocalDate.now().toString()))
            .count();
        int pendingInvoices = (int) InvoiceDB.getAll().stream()
            .filter(inv -> inv.getStatus() != Invoice.Status.PAID)
            .count();
        double totalRevenue = InvoiceDB.getAll().stream()
            .mapToDouble(Invoice::getAmountPaid)
            .sum();

        totalPatientsValue.setText(String.valueOf(totalPatients));
        todayAppointmentsValue.setText(String.valueOf(todayAppointments));
        pendingInvoicesValue.setText(String.valueOf(pendingInvoices));
        totalRevenueValue.setText(String.format("Rs. %.0f", totalRevenue));

        populateUpcomingAppointments();
    }
}
