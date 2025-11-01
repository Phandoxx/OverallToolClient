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
            String os = System.getProperty("os.name").toLowerCase();
            if (!os.contains("win")) {
                System.out.println("Not running on Windows — skipping shortcut creation. Found Operating System: " + os);
                return;
            }

            // Get the current running file path (jar or exe)
            String currentPath = new File(CreateShortcut.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI())
                    .getPath();

            // Path to user's startup folder
            String startupFolder = System.getenv("APPDATA")
                    + "\\Microsoft\\Windows\\Start Menu\\Programs\\Startup";

            // Full shortcut path
            String shortcutPath = startupFolder + "\\" + shortcutName + ".lnk";

            // Create a temporary VBScript file
            Path tempVbs = Files.createTempFile("createShortcut", ".vbs");

            // VBScript content to create the shortcut
            String vbs = """
                Set oWS = WScript.CreateObject("WScript.Shell")
                sLinkFile = "%s"
                Set oLink = oWS.CreateShortcut(sLinkFile)
                oLink.TargetPath = "%s"
                oLink.WorkingDirectory = "%s"
                oLink.Save
                """.formatted(shortcutPath.replace("\\", "\\\\"),
                    currentPath.replace("\\", "\\\\"),
                    new File(currentPath).getParent().replace("\\", "\\\\"));

            // Write the VBScript file
            Files.writeString(tempVbs, vbs);

            // Run it silently
            new ProcessBuilder("wscript", tempVbs.toString()).start().waitFor();

            System.out.println("✅ Shortcut created in Startup folder: " + shortcutPath);

            // Clean up temp file
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

