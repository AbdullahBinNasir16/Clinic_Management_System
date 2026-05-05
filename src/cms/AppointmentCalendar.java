package cms;

import javax.swing.*;
import java.awt.*;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class AppointmentCalendar extends JPanel {

    private LocalDate currentMonth;
    private JLabel monthLabel;

    public AppointmentCalendar() {
        currentMonth = LocalDate.now();
        setBackground(UIHelper.WHITE);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE0E8EE), 1),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        // Header with month/year and navigation
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIHelper.WHITE);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JButton prevBtn = UIHelper.secondaryButton("← Prev");
        prevBtn.addActionListener(e -> previousMonth());

        monthLabel = new JLabel();
        monthLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        monthLabel.setForeground(UIHelper.DARK_BLUE);
        monthLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton nextBtn = UIHelper.secondaryButton("Next →");
        nextBtn.addActionListener(e -> nextMonth());

        headerPanel.add(prevBtn, BorderLayout.WEST);
        headerPanel.add(monthLabel, BorderLayout.CENTER);
        headerPanel.add(nextBtn, BorderLayout.EAST);

        add(headerPanel);
        add(Box.createVerticalStrut(12));

        // Day labels (Sun-Sat)
        JPanel daysHeader = new JPanel(new GridLayout(1, 7, 4, 0));
        daysHeader.setBackground(UIHelper.WHITE);
        daysHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String day : dayNames) {
            JLabel dayLabel = new JLabel(day);
            dayLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            dayLabel.setForeground(UIHelper.MED_BLUE);
            dayLabel.setHorizontalAlignment(SwingConstants.CENTER);
            daysHeader.add(dayLabel);
        }
        add(daysHeader);
        add(Box.createVerticalStrut(8));

        // Calendar grid
        JPanel calendarGrid = new JPanel(new GridLayout(6, 7, 4, 4));
        calendarGrid.setBackground(UIHelper.WHITE);
        calendarGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        add(calendarGrid);

        updateCalendar(calendarGrid);
    }

    private void updateCalendar(JPanel calendarGrid) {
        calendarGrid.removeAll();
        monthLabel.setText(currentMonth.getMonth().toString() + " " + currentMonth.getYear());

        LocalDate firstDay = currentMonth.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDay = currentMonth.with(TemporalAdjusters.lastDayOfMonth());
        int startDow = firstDay.getDayOfWeek().getValue() % 7; // 0=Sunday
        int daysInMonth = lastDay.getDayOfMonth();

        // Empty cells before first day
        for (int i = 0; i < startDow; i++) {
            calendarGrid.add(new JLabel());
        }

        // Day cells
        for (int day = 1; day <= daysInMonth; day++) {
            JLabel dayCell = new JLabel(String.valueOf(day));
            dayCell.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            dayCell.setForeground(UIHelper.DARK_BLUE);
            dayCell.setHorizontalAlignment(SwingConstants.CENTER);
            dayCell.setVerticalAlignment(SwingConstants.CENTER);
            dayCell.setOpaque(true);
            dayCell.setBackground(new Color(0xF5F9FB));
            dayCell.setBorder(BorderFactory.createLineBorder(new Color(0xE6EEF2), 1));

            // Highlight today
            if (day == LocalDate.now().getDayOfMonth() && 
                currentMonth.getMonth() == LocalDate.now().getMonth() &&
                currentMonth.getYear() == LocalDate.now().getYear()) {
                dayCell.setBackground(UIHelper.CYAN_ACCENT);
                dayCell.setForeground(Color.WHITE);
                dayCell.setFont(new Font("Segoe UI", Font.BOLD, 12));
            }

            calendarGrid.add(dayCell);
        }

        // Empty cells after last day
        int totalCells = startDow + daysInMonth;
        int remainingCells = (totalCells % 7 == 0) ? 0 : 7 - (totalCells % 7);
        for (int i = 0; i < remainingCells; i++) {
            calendarGrid.add(new JLabel());
        }

        calendarGrid.revalidate();
        calendarGrid.repaint();
    }

    private void previousMonth() {
        currentMonth = currentMonth.minusMonths(1);
        JPanel calendarGrid = (JPanel) getComponent(3); // Grid is at index 3
        updateCalendar(calendarGrid);
    }

    private void nextMonth() {
        currentMonth = currentMonth.plusMonths(1);
        JPanel calendarGrid = (JPanel) getComponent(3);
        updateCalendar(calendarGrid);
    }
}
