package org.client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import org.ini4j.Wini;

public class Main {
    public static void main(String[] args) throws Exception {
        AtomicReference<Wini> ini = new AtomicReference<>(new Wini(new File("files/options/settings.ini")));
        AtomicReference<settings> settings = new AtomicReference<>(new settings("options/settings.ini"));

        org.server.MainFrame MainFrame = new org.server.MainFrame();
        MainFrame.setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();  // independent panel
        controlPanel.setBackground(new Color(51, 51, 51));

        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel settingsPanel = new JPanel();
        JPanel secondPanel = new JPanel();



        JButton stopButton = new JButton();
        stopButton.setText("Close/Stop");
        stopButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
        stopButton.setForeground(Color.WHITE);
        stopButton.setBackground(Color.DARK_GRAY);
        stopButton.setBounds(500, 100, 100, 30);
        controlPanel.add(stopButton);

        JButton startButton = new JButton();
        startButton.setText("Start");
        startButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
        startButton.setForeground(Color.WHITE);
        startButton.setBackground(Color.DARK_GRAY);
        startButton.setBounds(550, 100, 200, 30);
        controlPanel.add(startButton);

        JButton startupFolderButton = new JButton();
        startupFolderButton.setText("Run On Startup");
        startupFolderButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
        startupFolderButton.setForeground(Color.WHITE);
        startupFolderButton.setBackground(Color.DARK_GRAY);
        startupFolderButton.setBounds(0, 0, 100, 30);
        secondPanel.add(startupFolderButton);

        JButton removeStartupFolderButton = new JButton();
        removeStartupFolderButton.setText("Remove from Startup");
        removeStartupFolderButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
        removeStartupFolderButton.setForeground(Color.WHITE);
        removeStartupFolderButton.setBackground(Color.DARK_GRAY);
        removeStartupFolderButton.setBounds(0, 0, 100, 30);
        secondPanel.add(removeStartupFolderButton);

        MainFrame.add(controlPanel, BorderLayout.SOUTH);  // always visible at bottom

        secondPanel.setBorder(new EmptyBorder(10, 10, 10, 10));


        settingsPanel.setBackground(new Color(31, 31, 31));
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.X_AXIS));

        secondPanel.setBackground(new Color(31, 31, 31));
        secondPanel.setLayout(new BoxLayout(secondPanel, BoxLayout.X_AXIS));

        JPanel secondTab = new JPanel();
        secondTab.setLayout(new BoxLayout(secondTab, BoxLayout.X_AXIS));
        secondTab.setBorder(new EmptyBorder(10, 10, 10, 10));
        secondTab.setBackground(new Color(31, 31, 31));

        startupFolderButton.setAlignmentY(Component.TOP_ALIGNMENT);
        removeStartupFolderButton.setAlignmentY(Component.TOP_ALIGNMENT);

        secondTab.add(startupFolderButton);
        secondTab.add(Box.createHorizontalStrut(10));
        secondTab.add(removeStartupFolderButton);
        secondTab.add(Box.createHorizontalGlue()); // push buttons to left

        tabbedPane.addTab("Second", secondTab);

        tabbedPane.addTab("Settings", settingsPanel);
        MainFrame.add(tabbedPane, BorderLayout.CENTER);

        MainFrame.setVisible(true);

        startButton.addActionListener(e -> {
            Thread socketThread = new Thread(() -> {
                try {
                    SocketClient server = new SocketClient();
                    server.startClient();
                } catch (Exception ex) {
                    // Handle or log exceptions properly
                    ex.printStackTrace();
                }
            });

            socketThread.setName("SocketClient-Thread"); // optional, for clarity
            socketThread.start(); // start the thread
        });
        stopButton.addActionListener(e -> {
            System.exit(0);
        });
        startupFolderButton.addActionListener(e -> {
            System.out.println("No function added yet for addition");
        });
        removeStartupFolderButton.addActionListener(e -> {
            System.out.println("No function added yet for removal");
        });
    }
    public static class settings { // Retrieves settings
        private static final Properties props = new Properties();
        private static final File settingsFile = new File("files/options/settings.ini");
        private static long lastModified = 0; // track when file last changed

        public settings(String settingsPath) throws IOException {
            reload(); // load it once on creation
        }

        // Re-reads settings.ini if it has changed
        private static void reload() throws IOException {
            if (!settingsFile.exists()) throw new FileNotFoundException("settings.ini not found: " + settingsFile.getAbsolutePath());

            long modified = settingsFile.lastModified();
            if (modified != lastModified) { // only reload if the file actually changed
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
                reload(); // ensure we’re always up-to-date
            } catch (IOException e) {
                e.printStackTrace();
            }
            return props.getProperty(key, defaultValue);
        }
    }

    public class stringSetting { //Splits String into a String array based on the ","

        public static java.util.List<String> getAsList(String text) {
            String[] parts = text.split(",");
            java.util.List<String> result = new ArrayList<>();
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
    public static void saveSetting(Wini ini, String sectionName, String optionName, String input) { //Function to save settings to settings.ini
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
