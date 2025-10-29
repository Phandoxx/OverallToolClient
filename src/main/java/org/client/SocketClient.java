package org.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClient {
    public void startClient() {
        try {
            String ip = Main.settings.get("ip", "null");
            System.out.println("IP" + ip);
            if (ip.equalsIgnoreCase("")) { // if empty
                System.out.println("No IP given, cannot start");
                return;
            }

            Socket socket = new Socket(ip, 12345); // server IP and port
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            // Send a fixed message
            String message = "Hello from client!";
            output.println(message);
            output.flush(); // make sure it actually goes out
            System.out.println("Sent to server: " + message);

            // Optional: small delay to let server read the message before closing
            try { Thread.sleep(50); } catch (InterruptedException ignored) {}

            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
