package org.client;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame(){ //Sets the MainFrame for the GUI
        setTitle("Client Socket GUI");
        setSize(600, 280);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

    }
}