package org.client;

import org.ini4j.Wini;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

public class Main {
    public static void main(String[] args) throws Exception {
        // === Initialize Settings ===
        AtomicReference<Wini> ini = new AtomicReference<>(new Wini(new File("files/options/settings.ini")));
        AtomicReference<settings> settings = new AtomicReference<>(new settings("options/settings.ini"));

        // === Create Main Frame ===
        MainFrame mainFrame = new MainFrame();
        mainFrame.setLayout(new BorderLayout());

        // === Control Panel (Bottom Bar) ===
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(51, 51, 51));

        JButton startButton = createButton("Start", 100, 30);
        JButton stopButton = createButton("Close/Stop", 150, 30);

        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        mainFrame.add(controlPanel, BorderLayout.SOUTH);

        // === Tabs ===
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Settings", createSettingsPanel());
        tabbedPane.addTab("Apps", createAppsPanel());
        mainFrame.add(tabbedPane, BorderLayout.CENTER);

        mainFrame.setVisible(true);

        // === Button Actions ===
        startButton.addActionListener(e -> {
            Thread socketThread = new Thread(() -> {
                try {
                    SocketClient server = new SocketClient();
                    server.startClient();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            socketThread.setName("SocketClient-Thread");
            socketThread.start();
        });

        stopButton.addActionListener(e -> System.exit(0));
    }

    // -------------------------------------------------------
    // UI Components
    // -------------------------------------------------------

    private static JPanel createSettingsPanel() {
        JPanel settingsPanel = new JPanel();
        settingsPanel.setBackground(new Color(31, 31, 31));
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.X_AXIS));
        settingsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton startupFolderButton = createButton("Run On Startup", 200, 50);
        JButton removeStartupFolderButton = createButton("Remove from Startup", 200, 50);

        startupFolderButton.setAlignmentY(Component.TOP_ALIGNMENT);
        removeStartupFolderButton.setAlignmentY(Component.TOP_ALIGNMENT);

        startupFolderButton.addActionListener(e -> {
            CreateShortcut.createStartupShortcut("OverallToolsApp");
        });
        removeStartupFolderButton.addActionListener(e -> {
            CreateShortcut.removeStartupShortcut("OverallToolsApp");
        });

        settingsPanel.add(startupFolderButton);
        settingsPanel.add(Box.createHorizontalStrut(10));
        settingsPanel.add(removeStartupFolderButton);
        settingsPanel.add(Box.createHorizontalGlue());

        return settingsPanel;
    }

    private static JPanel createAppsPanel() {
        JPanel appsPanel = new JPanel();
        appsPanel.setBackground(new Color(31, 31, 31));
        appsPanel.setLayout(new BoxLayout(appsPanel, BoxLayout.X_AXIS));
        appsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton calendarButton = createButton("Calendar", 100, 50);
        calendarButton.setAlignmentY(Component.TOP_ALIGNMENT);

        calendarButton.addActionListener(e -> {
            CalenderApp.CalenderApp();
        });

        appsPanel.add(calendarButton);
        appsPanel.add(Box.createHorizontalStrut(10));
        appsPanel.add(Box.createHorizontalGlue());

        return appsPanel;
    }

    private static JButton createButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(new Font("Times New Roman", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(Color.DARK_GRAY);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(width, height));
        return button;
    }

    // -------------------------------------------------------
    // Settings Logic
    // -------------------------------------------------------

    public static class settings {
        private static final Properties props = new Properties();
        private static final File settingsFile = new File("files/options/settings.ini");
        private static long lastModified = 0;

        public settings(String settingsPath) throws IOException {
            reload();
        }

        private static void reload() throws IOException {
            if (!settingsFile.exists())
                throw new FileNotFoundException("settings.ini not found: " + settingsFile.getAbsolutePath());

            long modified = settingsFile.lastModified();
            if (modified != lastModified) {
                try (FileReader reader = new FileReader(settingsFile)) {
                    props.clear();
                    props.load(reader);
                    lastModified = modified;
                    System.out.println("🔄 Reloaded settings.ini (detected change)");
                }
            }
        }

        public static String get(String key, String defaultValue) {
            try {
                reload();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return props.getProperty(key, defaultValue);
        }
    }

    public class stringSetting {
        public static List<String> getAsList(String text) {
            String[] parts = text.split(",");
            List<String> result = new ArrayList<>();
            for (String part : parts) {
                String trimmed = part.trim();
                if (!trimmed.isEmpty()) result.add(trimmed);
            }
            return result;
        }

        public static String getAsString(String text) {
            List<String> list = getAsList(text);
            return String.join(" ", list);
        }
    }

    public static void saveSetting(Wini ini, String sectionName, String optionName, String input) {
        try {
            ini.put(sectionName, optionName, input);
            ini.store();
            System.out.println("Saved " + optionName + " to settings.ini");
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to save " + optionName + ": " + ex.getMessage());
        }
    }
}
