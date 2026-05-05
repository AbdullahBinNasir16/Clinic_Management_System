package cms;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;

public class UIHelper {

    public static final Color DARK_BLUE   = new Color(0x0C2A3E);
    public static final Color MED_BLUE    = new Color(0x227A8E);
    public static final Color CYAN_ACCENT = new Color(0x2AB7CA);
    public static final Color LIGHT_BLUE  = new Color(0x8DD5E1);
    public static final Color PALE_BLUE   = new Color(0xEAF7F8);
    public static final Color SURFACE     = new Color(0xF5F9FB);
    public static final Color WHITE       = Color.WHITE;
    public static final Color SUCCESS     = new Color(0x2E7D32);
    public static final Color ERROR       = new Color(0xC62828);
    public static final Color BORDER_GREY = new Color(0xD7E4EB);

    public static final Font TITLE_FONT    = new Font("Segoe UI", Font.BOLD,  24);
    public static final Font HEADER_FONT   = new Font("Segoe UI", Font.BOLD,  15);
    public static final Font LABEL_FONT    = new Font("Segoe UI", Font.BOLD,  12);
    public static final Font FIELD_FONT    = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font SMALL_FONT    = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    public static JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(LABEL_FONT);
        l.setForeground(DARK_BLUE);
        return l;
    }

    public static JTextField makeField() {
        JTextField f = new JTextField();
        f.setFont(FIELD_FONT);
        f.setBackground(WHITE);
        f.setCaretColor(DARK_BLUE);
        f.setOpaque(true);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xA7BACC), 1, true),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        f.setPreferredSize(new Dimension(260, 46));
        return f;
    }

    public static JTextArea makeTextArea(int rows, int cols) {
        JTextArea t = new JTextArea(rows, cols);
        t.setFont(FIELD_FONT);
        t.setLineWrap(true);
        t.setWrapStyleWord(true);
        t.setBackground(new Color(0xF8FCFD));
        t.setOpaque(true);
        t.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_GREY, 1, true),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        return t;
    }

    public static JComboBox<String> makeComboBox(String... items) {
        JComboBox<String> c = new JComboBox<>(items);
        c.setFont(FIELD_FONT);
        c.setBackground(WHITE);
        c.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
                return label;
            }
        });
        c.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_GREY, 1, true),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        return c;
    }

    public static JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(HEADER_FONT);
        b.setBackground(DARK_BLUE);
        b.setForeground(WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(11, 22, 11, 22));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(false);
        addHoverEffect(b, DARK_BLUE, MED_BLUE);
        return b;
    }

    public static JButton secondaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(FIELD_FONT);
        b.setBackground(MED_BLUE);
        b.setForeground(WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(false);
        addHoverEffect(b, MED_BLUE, LIGHT_BLUE.darker());
        return b;
    }

    private static void addHoverEffect(JButton b, Color normal, Color hover) {
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(hover); }
            @Override public void mouseExited(java.awt.event.MouseEvent e)  { b.setBackground(normal); }
        });
    }

    /** Single-arg header – no subtitle. */
    public static JPanel headerPanel(String title) {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, DARK_BLUE, getWidth(), getHeight(), MED_BLUE);
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        p.setLayout(new BorderLayout(0, 4));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        JLabel t = new JLabel(title);
        t.setFont(TITLE_FONT);
        t.setForeground(WHITE);
        p.add(t, BorderLayout.NORTH);
        return p;
    }

    public static JPanel headerPanel(String title, String subtitle) {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, DARK_BLUE, getWidth(), getHeight(), MED_BLUE);
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel t = new JLabel(title);
        t.setFont(TITLE_FONT);
        t.setForeground(WHITE);
        JLabel s = new JLabel(subtitle);
        s.setFont(SUBTITLE_FONT);
        s.setForeground(new Color(0xDDE5EC));
        p.add(t);
        p.add(Box.createVerticalStrut(8));
        p.add(s);
        return p;
    }

    public static JPanel card(String title) {
        JPanel p = new JPanel();
        p.setBackground(WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE0E8EE), 1),
            BorderFactory.createEmptyBorder(22, 22, 22, 22)));
        p.setLayout(new BorderLayout(0, 16));
        if (title != null) {
            JLabel lbl = new JLabel(title);
            lbl.setFont(HEADER_FONT);
            lbl.setForeground(DARK_BLUE);
            lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, CYAN_ACCENT),
                BorderFactory.createEmptyBorder(0, 0, 12, 0)));
            p.add(lbl, BorderLayout.NORTH);
        }
        return p;
    }

    public static void styleTable(JTable table) {
        table.setFont(FIELD_FONT);
        table.setRowHeight(32);
        table.setBackground(WHITE);
        table.setGridColor(new Color(0xE6EEF2));
        table.setSelectionBackground(new Color(0xD7EEFA));
        table.setSelectionForeground(DARK_BLUE);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setOpaque(true);
        header.setBackground(MED_BLUE);
        header.setForeground(WHITE);
        header.setFont(HEADER_FONT);
        header.setPreferredSize(new Dimension(header.getWidth(), 36));
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                          boolean isSelected, boolean hasFocus,
                                                          int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setOpaque(true);
                lbl.setBackground(MED_BLUE);
                lbl.setForeground(WHITE);
                lbl.setFont(HEADER_FONT);
                lbl.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
                return lbl;
            }
        });
    }

    public static void setLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        UIManager.put("TabbedPane.selected", WHITE);
        UIManager.put("TabbedPane.background", SURFACE);
        UIManager.put("TabbedPane.foreground", DARK_BLUE);
        UIManager.put("TabbedPane.selectedForeground", DARK_BLUE);
        UIManager.put("TabbedPane.unselectedForeground", new Color(0x4F6878));
        UIManager.put("TabbedPane.font", HEADER_FONT);
        UIManager.put("TabbedPane.contentAreaColor", SURFACE);
        UIManager.put("TabbedPane.light", PALE_BLUE);
        UIManager.put("TabbedPane.borderHightlightColor", MED_BLUE);
        UIManager.put("TabbedPane.darkShadow", new Color(0xCCCCCC));
        UIManager.put("Panel.background", SURFACE);
        UIManager.put("TextField.background", WHITE);
        UIManager.put("TextArea.background", WHITE);
        UIManager.put("ComboBox.background", WHITE);
        UIManager.put("Button.background", WHITE);
        UIManager.put("ScrollPane.background", SURFACE);
    }

    public static JPanel formRow(String label, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(WHITE);
        row.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        JLabel lbl = makeLabel(label);
        lbl.setPreferredSize(new Dimension(160, 28));
        lbl.setVerticalAlignment(SwingConstants.CENTER);
        row.add(lbl, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        return row;
    }
}
