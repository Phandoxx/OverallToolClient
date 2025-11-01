package org.client;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class CreateShortcut {

    /**
     * Creates a Windows Startup shortcut that points to the currently running .jar or .exe.
     * @param shortcutName The name of the shortcut (without .lnk)
     */
    public static void createStartupShortcut(String shortcutName) {
        try {
            // --- Detect OS ---
            String os = System.getProperty("os.name").toLowerCase();
            if (!os.contains("win")) {
                System.out.println("⛔ Not running on Windows — skipping shortcut creation.");
                return;
            }

            // --- Find current file (JAR or classpath) ---
            String currentPath = new File(CreateShortcut.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI())
                    .getPath();

            if (!currentPath.endsWith(".jar")) {
                System.out.println("⚠️ Not running from a JAR file — skipping shortcut creation.");
                return;
            }


            // --- Path to user's Startup folder ---
            String startupFolder = System.getenv("APPDATA") +
                    "\\Microsoft\\Windows\\Start Menu\\Programs\\Startup";

            // --- Name of shortcut ---
            String shortcutPath = startupFolder + "\\" + shortcutName + ".lnk";

            // --- Path to javaw.exe ---
            String javaPath = System.getProperty("java.home") + "\\bin\\javaw.exe";

            // --- Create temporary VBScript ---
            Path tempVbs = Files.createTempFile("createShortcut", ".vbs");

            // --- VBScript content ---
            String vbs = """
                    Set oWS = WScript.CreateObject("WScript.Shell")
                    sLinkFile = "%s"
                    Set oLink = oWS.CreateShortcut(sLinkFile)
                    oLink.TargetPath = "%s"
                    oLink.Arguments = "-jar ""%s""\"
                    oLink.WorkingDirectory = "%s"
                    oLink.Save
                """.formatted(
                    shortcutPath.replace("\\", "\\\\"),
                    javaPath.replace("\\", "\\\\"),
                    currentPath.replace("\\", "\\\\"),
                    new File(currentPath).getParent().replace("\\", "\\\\")
            );

            // --- Write VBScript to temp file ---
            Files.writeString(tempVbs, vbs);

            // --- Execute VBScript using Windows Script Host ---
            new ProcessBuilder("wscript", tempVbs.toString())
                    .inheritIO()
                    .start()
                    .waitFor();

            System.out.println("✅ Shortcut created in Startup folder: " + shortcutPath);

            // --- Clean up ---
            Files.deleteIfExists(tempVbs);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes a Windows Startup shortcut by name.
     * @param shortcutName The name of the shortcut (without .lnk)
     */
    public static void removeStartupShortcut(String shortcutName) {
        String shortcutPath = System.getenv("APPDATA")
                + "\\Microsoft\\Windows\\Start Menu\\Programs\\Startup\\"
                + shortcutName + ".lnk";

        File shortcut = new File(shortcutPath);
        if (shortcut.exists()) {
            if (shortcut.delete()) {
                System.out.println("🗑Removed startup shortcut: " + shortcutPath);
            } else {
                System.out.println("Failed to delete shortcut: " + shortcutPath);
            }
        } else {
            System.out.println("No shortcut found to remove.");
        }
    }
}

