package com.contactmanager.gui;

import com.contactmanager.model.User;
import com.contactmanager.service.UserService;

import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton registerButton;
    private JButton backButton;

    private UserService userService = new UserService();

    public RegisterFrame() {

        setTitle("Contact Manager - Register");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        initUI();

        setVisible(true);
    }

    private void initUI() {

        // ===== MAIN PANEL =====
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // ===== TITLE =====
        JLabel title = new JLabel("Create Account", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel subtitle = new JLabel("Register to get started", JLabel.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);

        JPanel titlePanel = new JPanel(new GridLayout(2,1));
        titlePanel.add(title);
        titlePanel.add(subtitle);

        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // ===== FORM =====
        JPanel form = new JPanel(new GridLayout(2, 2, 10, 15));

        form.add(new JLabel("Username"));
        usernameField = new JTextField();
        form.add(usernameField);

        form.add(new JLabel("Password"));
        passwordField = new JPasswordField();
        form.add(passwordField);

        mainPanel.add(form, BorderLayout.CENTER);

        // ===== BUTTON PANEL =====
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 10));

        registerButton = new JButton("Register");
        backButton = new JButton("Back");

        registerButton.setFocusPainted(false);
        backButton.setFocusPainted(false);

        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // ===== ACTIONS =====
        registerButton.addActionListener(e -> registerUser());
        backButton.addActionListener(e -> goBack());
    }

    private void registerUser() {

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // ✅ Validation
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }

        if (password.length() < 4) {
            JOptionPane.showMessageDialog(this, "Password must be at least 4 characters!");
            return;
        }

        boolean success = userService.register(new User(username, password));

        if (success) {
            JOptionPane.showMessageDialog(this, "Registration Successful!");
            dispose();
            new LoginFrame();
        } else {
            JOptionPane.showMessageDialog(this, "Username already exists!");
        }
    }

    private void goBack() {
        dispose();
        new LoginFrame();
    }
}