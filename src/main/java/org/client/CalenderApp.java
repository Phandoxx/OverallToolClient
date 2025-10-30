package org.client;

import javax.swing.*;
import java.awt.*;

public class CalenderApp {
    public static void CalenderApp() {
        org.client.CalenderFrame CalenderFrame = new org.client.CalenderFrame();
        CalenderFrame.setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();  // independent panel
        controlPanel.setBackground(new Color(51, 51, 51));

        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel settingsPanel = new JPanel();
        JPanel secondPanel = new JPanel();
        CalenderFrame.setVisible(true);
    }
}
