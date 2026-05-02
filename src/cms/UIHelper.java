package cms;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;

public class UIHelper {

    public static final Color DARK_BLUE  = new Color(0x1A5276);
    public static final Color MED_BLUE   = new Color(0x2D6A9F);
    public static final Color LIGHT_BLUE = new Color(0xD6EAF8);
    public static final Color PALE_BLUE  = new Color(0xEBF5FB);
    public static final Color WHITE      = Color.WHITE;
    public static final Color SUCCESS    = new Color(0x1E8449);
    public static final Color ERROR      = new Color(0xC0392B);
    public static final Color HEADER_BG  = new Color(0x2D6A9F);

    public static final Font TITLE_FONT  = new Font("Segoe UI", Font.BOLD,  20);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font LABEL_FONT  = new Font("Segoe UI", Font.BOLD,  12);
    public static final Font FIELD_FONT  = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font SMALL_FONT  = new Font("Segoe UI", Font.PLAIN, 11);

    public static JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(LABEL_FONT);
        l.setForeground(DARK_BLUE);
        return l;
    }

    public static JTextField makeField() {
        JTextField f = new JTextField();
        f.setFont(FIELD_FONT);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xAABBCC), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        f.setPreferredSize(new Dimension(220, 32));
        return f;
    }

    public static JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(HEADER_FONT);
        b.setBackground(MED_BLUE);
        b.setForeground(WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        return b;
    }

    public static JButton secondaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(FIELD_FONT);
        b.setBackground(LIGHT_BLUE);
        b.setForeground(DARK_BLUE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        return b;
    }

    /** Single-arg header – no subtitle. */
    public static JPanel headerPanel(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(DARK_BLUE);
        p.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));
        JLabel t = new JLabel(title);
        t.setFont(TITLE_FONT);
        t.setForeground(WHITE);
        p.add(t, BorderLayout.CENTER);
        return p;
    }

    /** Two-arg overload kept for compatibility; subtitle is silently dropped. */
    public static JPanel headerPanel(String title, String ignoredSubtitle) {
        return headerPanel(title);
    }

    public static JPanel card(String title) {
        JPanel p = new JPanel();
        p.setBackground(WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xD0E8F5), 1),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)));
        p.setLayout(new BorderLayout(0, 10));
        if (title != null) {
            JLabel lbl = new JLabel(title);
            lbl.setFont(HEADER_FONT);
            lbl.setForeground(MED_BLUE);
            lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, LIGHT_BLUE),
                BorderFactory.createEmptyBorder(0, 0, 8, 0)));
            p.add(lbl, BorderLayout.NORTH);
        }
        return p;
    }

    public static void styleTable(JTable table) {
        table.setFont(FIELD_FONT);
        table.setRowHeight(28);
        table.setGridColor(new Color(0xDDE8F0));
        table.setSelectionBackground(LIGHT_BLUE);
        table.setSelectionForeground(DARK_BLUE);
        table.setShowGrid(true);
        JTableHeader header = table.getTableHeader();
        header.setBackground(HEADER_BG);
        header.setForeground(WHITE);
        header.setFont(HEADER_FONT);
        header.setPreferredSize(new Dimension(header.getWidth(), 32));
    }

    public static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        // Ensure tab labels are always visible
        UIManager.put("TabbedPane.foreground",           DARK_BLUE);
        UIManager.put("TabbedPane.selectedForeground",   DARK_BLUE);
        UIManager.put("TabbedPane.unselectedForeground", new Color(0x333333));
        UIManager.put("TabbedPane.font",                 HEADER_FONT);
    }

    public static JPanel formRow(String label, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(WHITE);
        JLabel lbl = makeLabel(label);
        lbl.setPreferredSize(new Dimension(160, 30));
        row.add(lbl, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        return row;
    }
}
