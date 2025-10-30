package org.client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CalenderApp {
    public static void CalenderApp() {
        org.client.CalenderFrame CalenderFrame = new org.client.CalenderFrame();
        CalenderFrame.setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();  // independent panel
        controlPanel.setBackground(new Color(51, 51, 51));

        // Tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Array of months
        String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };

        // Create a tab for each month
        for (String month : months) {
            tabbedPane.addTab(month, createTab(month));
        }

        CalenderFrame.add(tabbedPane, BorderLayout.CENTER);

        CalenderFrame.setVisible(true);
    }
    private static JPanel createTab(String tabName) {
        // Rename the JPanel variable to avoid conflict
        JPanel panel = new JPanel();
        panel.setBackground(new Color(31, 31, 31));
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Right now it's empty; you can add buttons later
        // Example: top alignment is already set if you add components later
        // panel.add(Box.createHorizontalGlue()); // optional, keeps future components to the left

        return panel;
    }

}
