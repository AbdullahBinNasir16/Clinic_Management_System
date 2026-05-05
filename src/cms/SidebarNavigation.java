package cms;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class SidebarNavigation extends JPanel {

    private java.util.List<NavItem> navItems = new ArrayList<>();
    private NavItem selectedItem;
    private Runnable onSelectionChanged;

    private static class NavItem {
        String label;
        String icon;
        JLabel component;

        NavItem(String label) {
            this.label = label;
            this.icon = "• ";
        }
    }

    public SidebarNavigation() {
        setBackground(UIHelper.DARK_BLUE);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        setPreferredSize(new Dimension(200, 600));
    }

    public void addNavItem(String label) {
        NavItem item = new NavItem(label);
        JLabel itemLabel = new JLabel(item.icon + label);
        itemLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        itemLabel.setForeground(new Color(0xB0BEC5));
        itemLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        itemLabel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        itemLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        itemLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                itemLabel.setForeground(Color.WHITE);
                itemLabel.setBackground(new Color(0x1A3A4E));
                itemLabel.setOpaque(true);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (selectedItem != item) {
                    itemLabel.setForeground(new Color(0xB0BEC5));
                    itemLabel.setOpaque(false);
                }
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selectItem(item);
            }
        });

        item.component = itemLabel;
        navItems.add(item);
        add(itemLabel);
    }

    public void setOnSelectionChanged(Runnable callback) {
        this.onSelectionChanged = callback;
    }

    public void selectItem(int index) {
        if (index >= 0 && index < navItems.size()) {
            selectItem(navItems.get(index));
        }
    }

    private void selectItem(NavItem item) {
        if (selectedItem != null) {
            selectedItem.component.setForeground(new Color(0xB0BEC5));
            selectedItem.component.setOpaque(false);
        }

        selectedItem = item;
        item.component.setForeground(Color.WHITE);
        item.component.setBackground(UIHelper.CYAN_ACCENT);
        item.component.setOpaque(true);

        if (onSelectionChanged != null) {
            onSelectionChanged.run();
        }
    }

    public int getSelectedIndex() {
        if (selectedItem == null) return 0;
        for (int i = 0; i < navItems.size(); i++) {
            if (navItems.get(i) == selectedItem) {
                return i;
            }
        }
        return 0;
    }

    public void selectFirst() {
        selectItem(0);
    }
}
