package com.contactmanager;

import com.contactmanager.gui.LoginFrame;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {

        try {
            // 🔥 Apply FlatLaf Dark Theme
            FlatDarkLaf.setup();

            // 🎨 Global UI Improvements
            UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 14));

            // Rounded UI (Modern Look)
            UIManager.put("Button.arc", 15);
            UIManager.put("Component.arc", 15);
            UIManager.put("TextComponent.arc", 10);

        } catch (Exception e) {
            System.out.println("Failed to initialize FlatLaf");
        }

        // Run UI safely
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}